package com.pacmac.packetscope.data.nfc

import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import com.pacmac.packetscope.viewmodel.PacketRecord
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

class NfcReaderService(private val context: Context) {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    private val _packets = MutableSharedFlow<PacketRecord>()
    val packets: SharedFlow<PacketRecord> = _packets.asSharedFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _nfcStatus = MutableStateFlow<String?>(null)
    val nfcStatus: StateFlow<String?> = _nfcStatus.asStateFlow()

    init {
        updateNfcStatus()
    }

    private fun updateNfcStatus() {
        _nfcStatus.value = when {
            nfcAdapter == null -> "NFC not available on this device"
            !nfcAdapter.isEnabled -> "NFC is disabled. Enable it in Settings."
            _isListening.value -> "NFC Listening - Tap a tag"
            else -> "Tap 'Scan NFC' and hold a tag to the back of your phone"
        }
    }

    fun startListening() {
        if (nfcAdapter == null) {
            _nfcStatus.value = "NFC not available on this device"
            return
        }

        if (!nfcAdapter.isEnabled) {
            _nfcStatus.value = "NFC is disabled. Enable it in Settings."
            return
        }

        _isListening.value = true
        updateNfcStatus()
    }

    fun stopListening() {
        _isListening.value = false
        updateNfcStatus()
    }

    fun isNfcAvailable(): Boolean {
        return nfcAdapter != null
    }

    fun isNfcEnabled(): Boolean {
        return nfcAdapter?.isEnabled == true
    }

    fun handleTag(tag: Tag) {
        try {
            val ndef = Ndef.get(tag)

            if (ndef == null) {
                _nfcStatus.value = "Tag has no NDEF data"
                return
            }

            val packet = parseNdefTag(tag, ndef)
            _packets.tryEmit(packet)
            _nfcStatus.value = "Tag read successfully"

        } catch (e: IOException) {
            _nfcStatus.value = "Error reading tag: ${e.message}"
        } catch (e: Exception) {
            _nfcStatus.value = "Failed to read tag: ${e.message}"
        }
    }

    private fun parseNdefTag(tag: Tag, ndef: Ndef): PacketRecord {
        val uid = tag.id.toHexString()
        val techList = tag.techList.map { it.substringAfterLast('.') }

        // Connect to read NDEF message
        ndef.connect()
        val ndefMessage = ndef.ndefMessage
        ndef.close()

        val records = ndefMessage?.records ?: emptyArray()
        val recordDetails = mutableListOf<String>()
        var deviceName = "NFC Tag"
        val rawPayloadBytes = mutableListOf<Byte>()

        for ((index, record) in records.withIndex()) {
            val tnf = getTnfName(record.tnf)
            val type = String(record.type, Charset.forName("UTF-8"))
            val payload = record.payload

            // Collect raw bytes
            rawPayloadBytes.addAll(payload.toList())

            when {
                record.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(record.type, NdefRecord.RTD_TEXT) -> {
                    val text = parseTextRecord(record)
                    if (index == 0 && text.isNotEmpty()) {
                        deviceName = text.take(30)
                    }
                    recordDetails.add("Text: $text")
                }
                record.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(record.type, NdefRecord.RTD_URI) -> {
                    val uri = parseUriRecord(record)
                    recordDetails.add("URI: $uri")
                }
                else -> {
                    val payloadHex = payload.toHexString()
                    recordDetails.add("$tnf/$type: $payloadHex")
                }
            }
        }

        // Build service UUIDs list with NDEF record info
        val serviceUuids = recordDetails

        // Build manufacturer data with tech list
        val manufacturerData = mapOf(
            0 to "Tech: ${techList.joinToString(", ")}"
        )

        return PacketRecord(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            deviceName = deviceName,
            macAddress = uid, // Reuse macAddress field for UID
            rssi = 0, // Not applicable for NFC
            txPower = null,
            serviceUuids = serviceUuids,
            manufacturerData = manufacturerData,
            rawBytes = rawPayloadBytes.toByteArray(),
            protocol = "NFC"
        )
    }

    private fun parseTextRecord(record: NdefRecord): String {
        val payload = record.payload
        if (payload.isEmpty()) return ""

        // First byte contains encoding and language code length
        val languageCodeLength = payload[0].toInt() and 0x3F

        // Skip status byte and language code
        val textStartIndex = 1 + languageCodeLength
        if (textStartIndex >= payload.size) return ""

        // Determine encoding
        val isUtf16 = (payload[0].toInt() and 0x80) != 0
        val charset = if (isUtf16) Charsets.UTF_16 else Charsets.UTF_8

        return String(payload, textStartIndex, payload.size - textStartIndex, charset)
    }

    private fun parseUriRecord(record: NdefRecord): String {
        val payload = record.payload
        if (payload.isEmpty()) return ""

        val uriPrefixCode = payload[0].toInt() and 0xFF
        val uriPrefix = getUriPrefix(uriPrefixCode)
        val uriSuffix = String(payload, 1, payload.size - 1, Charsets.UTF_8)

        return uriPrefix + uriSuffix
    }

    private fun getTnfName(tnf: Short): String {
        val tnfInt = tnf.toInt()
        return when (tnfInt) {
            NdefRecord.TNF_EMPTY.toInt() -> "Empty"
            NdefRecord.TNF_WELL_KNOWN.toInt() -> "Well Known"
            NdefRecord.TNF_MIME_MEDIA.toInt() -> "MIME"
            NdefRecord.TNF_ABSOLUTE_URI.toInt() -> "URI"
            NdefRecord.TNF_EXTERNAL_TYPE.toInt() -> "External"
            NdefRecord.TNF_UNKNOWN.toInt() -> "Unknown"
            NdefRecord.TNF_UNCHANGED.toInt() -> "Unchanged"
            else -> "TNF:$tnf"
        }
    }

    private fun getUriPrefix(prefixCode: Int): String {
        return when (prefixCode) {
            0x00 -> ""
            0x01 -> "http://www."
            0x02 -> "https://www."
            0x03 -> "http://"
            0x04 -> "https://"
            0x05 -> "tel:"
            0x06 -> "mailto:"
            0x07 -> "ftp://anonymous:anonymous@"
            0x08 -> "ftp://ftp."
            0x09 -> "ftps://"
            0x0A -> "sftp://"
            0x0B -> "smb://"
            0x0C -> "nfs://"
            0x0D -> "ftp://"
            0x0E -> "dav://"
            0x0F -> "news:"
            0x10 -> "telnet://"
            0x11 -> "imap:"
            0x12 -> "rtsp://"
            0x13 -> "urn:"
            0x14 -> "pop:"
            0x15 -> "sip:"
            0x16 -> "sips:"
            0x17 -> "tftp:"
            0x18 -> "btspp://"
            0x19 -> "btl2cap://"
            0x1A -> "btgoep://"
            0x1B -> "tcpobex://"
            0x1C -> "irdaobex://"
            0x1D -> "file://"
            0x1E -> "urn:epc:id:"
            0x1F -> "urn:epc:tag:"
            0x20 -> "urn:epc:pat:"
            0x21 -> "urn:epc:raw:"
            0x22 -> "urn:epc:"
            0x23 -> "urn:nfc:"
            else -> ""
        }
    }

    private fun ByteArray.toHexString(): String {
        return joinToString(":") { "%02X".format(it) }
    }
}

package com.pacmac.packetscope.viewmodel

import android.app.Application
import android.nfc.Tag
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.packetscope.data.ble.BleScannerService
import com.pacmac.packetscope.data.nfc.NfcReaderService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PacketRecord(
    val id: String,
    val timestamp: Long,
    val deviceName: String,
    val macAddress: String,
    val rssi: Int,
    val txPower: Int?,
    val serviceUuids: List<String>,
    val manufacturerData: Map<Int, String>,
    val rawBytes: ByteArray,
    val protocol: String = "BLE"
) {
    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (javaClass != other?.javaClass) return false
        other as PacketRecord
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

data class ScannerState(
    val isScanning: Boolean = false,
    val packets: List<PacketRecord> = emptyList(),
    val error: String? = null
)

class ScannerViewModel(application: Application) : AndroidViewModel(application) {
    private val bleScanner = BleScannerService(application)
    private val nfcReader = NfcReaderService(application)

    private val _state = MutableStateFlow(ScannerState())
    val state: StateFlow<ScannerState> = _state.asStateFlow()

    val isScanning: StateFlow<Boolean> = bleScanner.isScanning
    val scanError: StateFlow<String?> = bleScanner.scanError

    val isNfcListening: StateFlow<Boolean> = nfcReader.isListening
    val nfcStatus: StateFlow<String?> = nfcReader.nfcStatus

    private val packetMap = mutableMapOf<String, PacketRecord>()
    private val maxPackets = 100

    init {
        // Collect BLE packets
        viewModelScope.launch {
            bleScanner.packets.collect { packet ->
                addPacket(packet)
            }
        }

        // Collect NFC packets
        viewModelScope.launch {
            nfcReader.packets.collect { packet ->
                addPacket(packet)
            }
        }

        viewModelScope.launch {
            bleScanner.isScanning.collect { scanning ->
                _state.value = _state.value.copy(isScanning = scanning)
            }
        }

        viewModelScope.launch {
            bleScanner.scanError.collect { error ->
                _state.value = _state.value.copy(error = error)
            }
        }
    }

    private fun addPacket(packet: PacketRecord) {
        val key = if (packet.protocol == "NFC") {
            "${packet.macAddress}_${packet.timestamp}" // NFC: deduplicate by UID + timestamp
        } else {
            packet.macAddress // BLE: deduplicate by MAC address
        }

        val existing = packetMap[key]
        if (existing != null && packet.protocol == "BLE") {
            // Update existing BLE packet with new RSSI and timestamp
            val updated = packet.copy(
                id = existing.id,
                timestamp = System.currentTimeMillis()
            )
            packetMap[key] = updated
        } else {
            // Add new packet (NFC or new BLE device)
            packetMap[key] = packet

            // Keep only most recent 100
            if (packetMap.size > maxPackets) {
                val oldest = packetMap.values.minByOrNull { it.timestamp }
                oldest?.let {
                    val oldKey = if (it.protocol == "NFC") {
                        "${it.macAddress}_${it.timestamp}"
                    } else {
                        it.macAddress
                    }
                    packetMap.remove(oldKey)
                }
            }
        }

        // Emit sorted by most recent
        _state.value = _state.value.copy(
            packets = packetMap.values.sortedByDescending { it.timestamp }
        )
    }

    fun startScan() {
        bleScanner.startScan()
    }

    fun stopScan() {
        bleScanner.stopScan()
    }

    fun startNfcListening() {
        nfcReader.startListening()
    }

    fun stopNfcListening() {
        nfcReader.stopListening()
    }

    fun isNfcAvailable(): Boolean {
        return nfcReader.isNfcAvailable()
    }

    fun isNfcEnabled(): Boolean {
        return nfcReader.isNfcEnabled()
    }

    fun handleNfcTag(tag: Tag) {
        nfcReader.handleTag(tag)
    }

    override fun onCleared() {
        super.onCleared()
        bleScanner.stopScan()
        nfcReader.stopListening()
    }
}

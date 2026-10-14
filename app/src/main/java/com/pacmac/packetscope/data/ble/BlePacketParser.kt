package com.pacmac.packetscope.data.ble

import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import com.pacmac.packetscope.viewmodel.PacketRecord
import java.util.UUID

object BlePacketParser {
    fun parse(result: ScanResult): PacketRecord {
        val scanRecord = result.scanRecord
        val device = result.device
        val rssi = result.rssi
        val timestamp = System.currentTimeMillis()

        val deviceName = scanRecord?.deviceName ?: device.name ?: "Unknown"
        val macAddress = device.address ?: "Unknown"
        val txPower = scanRecord?.txPowerLevel
        val serviceUuids = scanRecord?.serviceUuids?.map { it.toString() } ?: emptyList()
        val manufacturerData = parseManufacturerData(scanRecord)
        val rawBytes = scanRecord?.bytes ?: byteArrayOf()

        return PacketRecord(
            id = UUID.randomUUID().toString(),
            timestamp = timestamp,
            deviceName = deviceName,
            macAddress = macAddress,
            rssi = rssi,
            txPower = txPower,
            serviceUuids = serviceUuids,
            manufacturerData = manufacturerData,
            rawBytes = rawBytes,
            protocol = "BLE"
        )
    }

    private fun parseManufacturerData(scanRecord: ScanRecord?): Map<Int, String> {
        val result = mutableMapOf<Int, String>()

        scanRecord?.manufacturerSpecificData?.let { sparseArray ->
            for (i in 0 until sparseArray.size()) {
                val key = sparseArray.keyAt(i)
                val value = sparseArray.get(key)
                result[key] = value.toHexString()
            }
        }

        return result
    }

    private fun ByteArray.toHexString(): String {
        return joinToString(" ") { byte -> "%02X".format(byte) }
    }
}

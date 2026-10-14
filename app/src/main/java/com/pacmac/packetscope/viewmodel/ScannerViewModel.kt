package com.pacmac.packetscope.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.packetscope.data.ble.BleScannerService
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

    private val _state = MutableStateFlow(ScannerState())
    val state: StateFlow<ScannerState> = _state.asStateFlow()

    val isScanning: StateFlow<Boolean> = bleScanner.isScanning
    val scanError: StateFlow<String?> = bleScanner.scanError

    private val packetMap = mutableMapOf<String, PacketRecord>()
    private val maxPackets = 100

    init {
        viewModelScope.launch {
            bleScanner.packets.collect { packet ->
                val existing = packetMap[packet.macAddress]
                if (existing != null) {
                    // Update existing packet with new RSSI and timestamp
                    val updated = packet.copy(
                        id = existing.id,
                        timestamp = System.currentTimeMillis()
                    )
                    packetMap[packet.macAddress] = updated
                } else {
                    // Add new packet
                    packetMap[packet.macAddress] = packet

                    // Keep only most recent 100
                    if (packetMap.size > maxPackets) {
                        val oldest = packetMap.values.minByOrNull { it.timestamp }
                        oldest?.let { packetMap.remove(it.macAddress) }
                    }
                }

                // Emit sorted by most recent
                _state.value = _state.value.copy(
                    packets = packetMap.values.sortedByDescending { it.timestamp }
                )
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

    fun startScan() {
        bleScanner.startScan()
    }

    fun stopScan() {
        bleScanner.stopScan()
    }

    override fun onCleared() {
        super.onCleared()
        bleScanner.stopScan()
    }
}

package com.pacmac.packetscope.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PacketData(
    val id: String,
    val deviceName: String,
    val rssi: Int,
    val timestamp: Long
)

data class ScannerState(
    val isScanning: Boolean = false,
    val packets: List<PacketData> = emptyList()
)

class ScannerViewModel : ViewModel() {
    private val _state = MutableStateFlow(ScannerState())
    val state: StateFlow<ScannerState> = _state.asStateFlow()
}

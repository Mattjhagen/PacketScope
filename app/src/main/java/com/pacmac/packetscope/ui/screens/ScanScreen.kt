package com.pacmac.packetscope.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pacmac.packetscope.R
import com.pacmac.packetscope.viewmodel.PacketRecord
import com.pacmac.packetscope.viewmodel.ScannerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: ScannerViewModel,
    navController: NavController? = null
) {
    val state by viewModel.state.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val scanError by viewModel.scanError.collectAsState()
    val isNfcListening by viewModel.isNfcListening.collectAsState()
    val nfcStatus by viewModel.nfcStatus.collectAsState()
    val saveSessionMessage by viewModel.saveSessionMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saveSessionMessage) {
        saveSessionMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSaveSessionMessage()
        }
    }

    // Filter state
    var protocolFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var rssiFilter by remember { mutableStateOf("All") }

    // Apply filters
    val filteredPackets = state.packets.filter { packet ->
        // Protocol filter
        val matchesProtocol = when (protocolFilter) {
            "BLE" -> packet.protocol == "BLE"
            "NFC" -> packet.protocol == "NFC"
            else -> true
        }

        // Search filter
        val matchesSearch = searchQuery.isEmpty() ||
                packet.deviceName.contains(searchQuery, ignoreCase = true) ||
                packet.macAddress.contains(searchQuery, ignoreCase = true)

        // RSSI filter (only for BLE)
        val matchesRssi = when (rssiFilter) {
            "-60 and stronger" -> packet.protocol == "NFC" || packet.rssi >= -60
            "-80 and stronger" -> packet.protocol == "NFC" || packet.rssi >= -80
            else -> true
        }

        matchesProtocol && matchesSearch && matchesRssi
    }

    // Calculate counts for chips
    val totalDevices = state.packets.size
    val veryClose = state.packets.count { it.rssi >= -60 }
    val unnamedStrong = state.packets.count {
        (it.deviceName.contains("Unknown", ignoreCase = true) ||
         it.deviceName.isBlank()) && it.rssi >= -60
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color(0xFF0B0B14)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Header with status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RF Detector",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Surface(
                        color = if (isScanning) Color(0xFF00D8FF).copy(alpha = 0.2f) else Color(0xFF9AA0B4).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (isScanning) Color(0xFF00D8FF) else Color(0xFF9AA0B4),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                            Text(
                                text = if (isScanning) "LIVE" else "IDLE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isScanning) Color(0xFF00D8FF) else Color(0xFF9AA0B4)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Count chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CountChip(
                        label = "$totalDevices devices",
                        modifier = Modifier.weight(1f)
                    )
                    CountChip(
                        label = "$veryClose very close",
                        modifier = Modifier.weight(1f)
                    )
                    CountChip(
                        label = "$unnamedStrong new to you",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (isScanning) {
                        viewModel.stopScan()
                    } else {
                        viewModel.startScan()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isScanning) Color(0xFFFF3B30) else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isScanning) "Stop Scan" else stringResource(R.string.start_scan),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Button(
                onClick = {
                    if (isNfcListening) {
                        viewModel.stopNfcListening()
                    } else {
                        viewModel.startNfcListening()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isNfcListening) Color(0xFFFF3B30) else Color(0xFF9C27B0)
                ),
                enabled = viewModel.isNfcAvailable() && viewModel.isNfcEnabled()
            ) {
                Text(
                    text = if (isNfcListening) "Stop NFC" else stringResource(R.string.scan_nfc),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Save Session button (show when stopped and has packets)
        if (!isScanning && !isNfcListening && state.packets.isNotEmpty()) {
            OutlinedButton(
                onClick = { viewModel.saveCurrentSession() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF2ECC71)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Session (${state.packets.size} packets)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // NFC status message
        if (!viewModel.isNfcAvailable()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "NFC not available on this device",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        } else if (!viewModel.isNfcEnabled()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "NFC is disabled. Enable it in Settings.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        } else if (isNfcListening && nfcStatus != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF9C27B0).copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = nfcStatus!!,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9C27B0)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Error message
        scanError?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Filter section
        if (state.packets.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Protocol filter chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = protocolFilter == "All",
                            onClick = { protocolFilter = "All" },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = protocolFilter == "BLE",
                            onClick = { protocolFilter = "BLE" },
                            label = { Text("BLE") }
                        )
                        FilterChip(
                            selected = protocolFilter == "NFC",
                            onClick = { protocolFilter = "NFC" },
                            label = { Text("NFC") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Search field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search name, UID, or MAC...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // RSSI filter chips
                    Text(
                        text = "RSSI Filter (BLE only):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = rssiFilter == "All",
                            onClick = { rssiFilter = "All" },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = rssiFilter == "-80 and stronger",
                            onClick = { rssiFilter = "-80 and stronger" },
                            label = { Text("-80 dBm+") }
                        )
                        FilterChip(
                            selected = rssiFilter == "-60 and stronger",
                            onClick = { rssiFilter = "-60 and stronger" },
                            label = { Text("-60 dBm+") }
                        )
                    }

                    // Show filter results count
                    if (filteredPackets.size != state.packets.size) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Showing ${filteredPackets.size} of ${state.packets.size} packets",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Device list
        if (filteredPackets.isEmpty() && !isScanning && !isNfcListening) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state.packets.isEmpty()) {
                        "No devices found yet. Start a BLE scan or tap an NFC tag."
                    } else {
                        "No packets match the current filters."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPackets, key = { it.id }) { packet ->
                    DeviceItem(
                        packet = packet,
                        onClick = {
                            navController?.navigate("detail/${packet.id}")
                        }
                    )
                }
            }
        }
            }
        }
    }
}

@Composable
private fun DeviceItem(
    packet: PacketRecord,
    onClick: () -> Unit
) {
    val isNfc = packet.protocol == "NFC"
    val protocolColor = if (isNfc) Color(0xFF9C27B0) else Color(0xFF00BCD4)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Protocol badge and name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = protocolColor,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = packet.protocol,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                    Text(
                        text = packet.deviceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (!isNfc) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${packet.rssi} dBm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        SignalStrengthBar(rssi = packet.rssi)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // MAC address / UID
            Text(
                text = if (isNfc) "UID: ${packet.macAddress}" else packet.macAddress,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Show first NDEF record preview for NFC
            if (isNfc && packet.serviceUuids.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = packet.serviceUuids.first().take(100),
                    style = MaterialTheme.typography.bodySmall,
                    color = protocolColor,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Distance/timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!isNfc) {
                    Text(
                        text = getDistanceLabel(packet.rssi),
                        style = MaterialTheme.typography.bodySmall,
                        color = getDistanceColor(packet.rssi)
                    )
                } else {
                    Text(
                        text = "${packet.rawBytes.size} bytes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = formatTimestamp(packet.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SignalStrengthBar(rssi: Int) {
    val bars = when {
        rssi >= -60 -> 3
        rssi >= -80 -> 2
        else -> 1
    }

    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(12.dp + (index * 4).dp)
            ) {
                Surface(
                    color = if (index < bars) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

private fun getDistanceLabel(rssi: Int): String {
    return when {
        rssi >= -60 -> "Very close"
        rssi >= -80 -> "Close"
        else -> "Far"
    }
}

@Composable
private fun getDistanceColor(rssi: Int): Color {
    return when {
        rssi >= -60 -> MaterialTheme.colorScheme.primary
        rssi >= -80 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
private fun CountChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(32.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF262636),
                shape = RoundedCornerShape(16.dp)
            ),
        color = Color(0xFF1A1A26),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

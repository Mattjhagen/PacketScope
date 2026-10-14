package com.pacmac.packetscope.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pacmac.packetscope.R
import com.pacmac.packetscope.viewmodel.PacketRecord
import com.pacmac.packetscope.viewmodel.ScannerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isScanning) "BLE: LIVE" else "BLE: IDLE",
                style = MaterialTheme.typography.titleMedium,
                color = if (isScanning) Color(0xFF00BCD4) else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (isNfcListening) "NFC: LISTENING" else "NFC: OFF",
                style = MaterialTheme.typography.titleMedium,
                color = if (isNfcListening) Color(0xFF9C27B0) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        // Device list
        if (state.packets.isEmpty() && !isScanning && !isNfcListening) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No devices found yet. Start a BLE scan or tap an NFC tag.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.packets, key = { it.id }) { packet ->
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

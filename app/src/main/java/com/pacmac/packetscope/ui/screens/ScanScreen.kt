package com.pacmac.packetscope.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
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
    val saveSessionMessage by viewModel.saveSessionMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saveSessionMessage) {
        saveSessionMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSaveSessionMessage()
        }
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
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Header with status - clean, no toolbar clutter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                            color = if (isScanning) Color(0xFFFF3B30).copy(alpha = 0.2f) else Color(0xFF9AA0B4).copy(alpha = 0.2f),
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
                                            if (isScanning) Color(0xFFFF3B30) else Color(0xFF9AA0B4),
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = if (isScanning) "LIVE" else "IDLE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isScanning) Color(0xFF00D8FF) else Color(0xFF9AA0B4)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isScanning || isNfcListening) {
                    // Count chips when scanning
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
                }

                // NFC instruction banner when NFC listening
                if (isNfcListening) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF8B5CF6).copy(alpha = 0.08f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFF8B5CF6))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Nfc,
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Tap the NFC button and choose Scan NFC. Hold the tag against the top-back of your phone.",
                                fontSize = 14.sp,
                                color = Color.White,
                                lineHeight = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { viewModel.stopNfcListening() },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFFFF3B30)
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Error message
                scanError?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF3B30).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp,
                            color = Color(0xFFFF3B30)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Main content area
                if (!isScanning && !isNfcListening) {
                    // IDLE state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = Color(0xFF9AA0B4),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "Scanner is idle",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Tap Start Scan to detect nearby signals",
                                fontSize = 16.sp,
                                color = Color(0xFF9AA0B4)
                            )
                        }
                    }

                    // Bottom buttons when idle
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (viewModel.isNfcAvailable() && viewModel.isNfcEnabled()) {
                                    viewModel.startNfcListening()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8B5CF6)
                            ),
                            shape = RoundedCornerShape(28.dp),
                            enabled = viewModel.isNfcAvailable() && viewModel.isNfcEnabled()
                        ) {
                            Text(
                                text = stringResource(R.string.scan_nfc),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Button(
                            onClick = { viewModel.startScan() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00D8FF)
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.start_scan),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    // LIVE state - scanning
                    if (state.packets.isEmpty()) {
                        // LIVE empty state
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SignalCellularConnectedNoInternet0Bar,
                                    contentDescription = null,
                                    tint = Color(0xFF9AA0B4),
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    text = "No signals detected",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Searching for signals...",
                                    fontSize = 16.sp,
                                    color = Color(0xFF9AA0B4)
                                )
                            }
                        }
                    } else {
                        // LIVE with devices
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 96.dp)
                        ) {
                            items(state.packets, key = { it.id }) { packet ->
                                DeviceCard(
                                    packet = packet,
                                    onClick = {
                                        navController?.navigate("detail/${packet.id}")
                                    },
                                    onLocate = { /* TODO */ },
                                    onMarkThreat = { viewModel.markAsThreat(packet.macAddress, true) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating buttons when scanning
        if (isScanning || isNfcListening) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (isScanning) viewModel.stopScan()
                        if (isNfcListening) viewModel.stopNfcListening()
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF3B30)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                if (isScanning) {
                    Button(
                        onClick = { viewModel.stopScan() },
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 140.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF3B30)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Stop Scan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(32.dp),
        color = Color(0xFF1A1A26),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF262636))
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
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

@Composable
private fun DeviceCard(
    packet: PacketRecord,
    onClick: () -> Unit,
    onLocate: () -> Unit,
    onMarkThreat: () -> Unit
) {
    val isNfc = packet.protocol == "NFC"
    val protocolColor = if (isNfc) Color(0xFF8B5CF6) else Color(0xFF00D8FF)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A26)
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFF262636))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1: Icon, Name, Proximity, RSSI, Signal bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Surface(
                    modifier = Modifier.size(40.dp),
                    color = Color(0xFF262636),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isNfc) Icons.Default.Nfc else Icons.Default.Wifi,
                            contentDescription = null,
                            tint = protocolColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Name
                Text(
                    text = packet.deviceName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Proximity & RSSI for BLE
                if (!isNfc) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = getProximityLabel(packet.rssi),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00D8FF)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${packet.rssi} dBm",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            SignalBars(rssi = packet.rssi)
                        }
                    }
                }
            }

            // Row 2: MAC/UID and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isNfc) packet.macAddress else packet.macAddress,
                    fontSize = 12.sp,
                    color = Color(0xFF9AA0B4),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "SIGNAL | LAST ${getTimeAgo(packet.timestamp)}",
                    fontSize = 12.sp,
                    color = Color(0xFF9AA0B4)
                )
            }

            // Row 3: Chips and badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // New chip
                OutlinedChip(
                    label = "New",
                    color = Color(0xFF00D8FF)
                )
                // First time seen chip
                OutlinedChip(
                    label = "First time seen",
                    color = Color(0xFF8B5CF6)
                )
                // Protocol badge
                Surface(
                    color = Color(0xFF262636),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isNfc) "NFC" else "WIFI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Row 4: Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLocate,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D8FF)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Locate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color(0xFF3A3A4A)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "View details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Row 5: Mark threat button (full width)
            Button(
                onClick = onMarkThreat,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Mark threat",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun OutlinedChip(
    label: String,
    color: Color
) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SignalBars(rssi: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val bars = when {
            rssi >= -60 -> 3
            rssi >= -80 -> 2
            else -> 1
        }

        for (i in 1..3) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((i * 4).dp)
                    .background(
                        if (i <= bars) Color(0xFF00D8FF) else Color(0xFF3A3A4A),
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

private fun getProximityLabel(rssi: Int): String {
    return when {
        rssi >= -60 -> "Very close"
        rssi >= -80 -> "Close"
        else -> "Far"
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val seconds = (System.currentTimeMillis() - timestamp) / 1000
    return "${seconds}s"
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

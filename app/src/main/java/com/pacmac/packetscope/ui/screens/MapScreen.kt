package com.pacmac.packetscope.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pacmac.packetscope.data.local.ScanSession
import com.pacmac.packetscope.viewmodel.ScannerViewModel

@Composable
fun MapScreen(
    viewModel: ScannerViewModel = viewModel()
) {
    val sessions by viewModel.repository.getAllSessions().collectAsState(initial = emptyList())
    val sessionsWithLocation = sessions.filter { it.locationLat != null && it.locationLon != null }

    var selectedFilter by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14))
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Header
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
                    text = "RF Map",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Surface(
                    color = Color(0xFF9AA0B4).copy(alpha = 0.2f),
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
                                .background(Color(0xFF9AA0B4), shape = CircleShape)
                        )
                        Text(
                            text = "IDLE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9AA0B4)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                label = "WIFI",
                selected = selectedFilter == "WIFI",
                onClick = { selectedFilter = "WIFI" }
            )
            FilterChip(
                label = "BLUETOOTH",
                selected = selectedFilter == "BLUETOOTH",
                onClick = { selectedFilter = "BLUETOOTH" }
            )
            FilterChip(
                label = "CELLULAR",
                selected = selectedFilter == "CELLULAR",
                onClick = { selectedFilter = "CELLULAR" }
            )
            FilterChip(
                label = "NFC",
                selected = selectedFilter == "NFC",
                onClick = { selectedFilter = "NFC" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Count pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                color = Color(0xFF1A1A26),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF00D8FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${sessionsWithLocation.size} mapped signals",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Map area or empty state
        if (sessionsWithLocation.isEmpty()) {
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
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF9AA0B4),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No saved locations yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Save a session to see it here.",
                        fontSize = 16.sp,
                        color = Color(0xFF9AA0B4)
                    )
                }
            }
        } else {
            // Map placeholder with styled background
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A26)
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF262636))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Placeholder for map visualization
                        Text(
                            text = "Map View",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00D8FF)
                        )
                        Text(
                            text = "${sessionsWithLocation.size} locations mapped",
                            fontSize = 14.sp,
                            color = Color(0xFF9AA0B4)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Show session list
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            sessionsWithLocation.forEach { session ->
                                LocationCard(session)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.height(32.dp),
        color = if (selected) Color(0xFF00D8FF).copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) Color(0xFF00D8FF) else Color(0xFF262636)
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) Color(0xFF00D8FF) else Color(0xFF9AA0B4)
            )
        }
    }
}

@Composable
private fun LocationCard(session: ScanSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A26)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF262636))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF00D8FF),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = session.type,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "${session.count} devices",
                    fontSize = 14.sp,
                    color = Color(0xFF9AA0B4)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lat: ${String.format("%.4f", session.locationLat)}, " +
                        "Lon: ${String.format("%.4f", session.locationLon)}",
                fontSize = 12.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = Color(0xFF9AA0B4)
            )

            Text(
                text = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(session.timestamp)),
                fontSize = 12.sp,
                color = Color(0xFF9AA0B4)
            )
        }
    }
}

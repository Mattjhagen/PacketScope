package com.pacmac.packetscope.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pacmac.packetscope.data.local.ScanSession
import com.pacmac.packetscope.viewmodel.ScannerViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MapScreen(
    viewModel: ScannerViewModel = viewModel()
) {
    val sessions by viewModel.repository.getAllSessions().collectAsState(initial = emptyList())
    val sessionsWithLocation = sessions.filter { it.locationLat != null && it.locationLon != null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14))
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Saved Locations",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "${sessionsWithLocation.size} sessions with location",
            fontSize = 14.sp,
            color = Color(0xFF9AA0B4)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (sessionsWithLocation.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Save a session to record its location",
                        fontSize = 14.sp,
                        color = Color(0xFF9AA0B4)
                    )
                }
            }
        } else {
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

@Composable
private fun LocationCard(session: ScanSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A26)
        ),
        shape = RoundedCornerShape(12.dp)
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

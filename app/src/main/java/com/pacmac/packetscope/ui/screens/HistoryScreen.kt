package com.pacmac.packetscope.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Scans", "Devices", "Threats")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "History",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "0 sessions",
                    fontSize = 14.sp,
                    color = Color(0xFF9AA0B4)
                )
            }

            Button(
                onClick = { /* Export */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D8FF)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export", color = Color.Black, fontSize = 14.sp)
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF12121E),
            contentColor = Color(0xFF00D8FF)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tab content - empty states
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = if (selectedTab == 2) Icons.Default.Search else Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF9AA0B4),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = when (selectedTab) {
                        0 -> "No scan sessions yet"
                        1 -> "No devices recorded"
                        2 -> "No threats flagged"
                        else -> ""
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = when (selectedTab) {
                        0 -> "Start a scan to begin recording"
                        1 -> "Devices from scans will appear here"
                        2 -> "Flag devices from scans as threats"
                        else -> ""
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF9AA0B4)
                )
            }
        }
    }
}

package com.pacmac.packetscope.ui.detect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LensGlintScreen(navController: NavController) {
    var flashOn by remember { mutableStateOf(false) }
    var previewActive by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Surface(
                    color = Color(0xFF9AA0B4).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (previewActive) "ACTIVE" else "IDLE",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (previewActive) Color(0xFF00D8FF) else Color(0xFF9AA0B4)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Lens Glint Check",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Spot lens reflections",
                fontSize = 14.sp,
                color = Color(0xFF9AA0B4)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Preview placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A26)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (previewActive) "Camera Preview Active\n(Live view would appear here)" else "Preview Inactive",
                        fontSize = 16.sp,
                        color = Color(0xFF9AA0B4),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Disclaimer
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFA726).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Glint check helps spot exposed lenses, not all cameras.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = Color(0xFFFFA726)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Steps
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A26)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How to use:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StepItem("1", "Darken the room")
                    StepItem("2", "Turn on flash below")
                    StepItem("3", "Start preview and slowly scan room")
                    StepItem("4", "Look for bright pin-point reflections")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Flashlight",
                    fontSize = 16.sp,
                    color = Color.White
                )
                Switch(
                    checked = flashOn,
                    onCheckedChange = { flashOn = it },
                    enabled = previewActive,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00D8FF),
                        checkedTrackColor = Color(0xFF00D8FF).copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start/Stop button
            Button(
                onClick = { previewActive = !previewActive },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (previewActive) Color(0xFFFF3B30) else Color(0xFF00D8FF)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (previewActive) "Stop Preview" else "Start Preview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (previewActive) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
private fun StepItem(number: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = Color(0xFF00D8FF).copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00D8FF)
                )
            }
        }
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF9AA0B4),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

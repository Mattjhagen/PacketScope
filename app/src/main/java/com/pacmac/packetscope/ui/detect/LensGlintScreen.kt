package com.pacmac.packetscope.ui.detect

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LensGlintScreen(navController: NavController) {
    var isScanning by remember { mutableStateOf(false) }
    var expandedGuidance by remember { mutableStateOf(true) }
    val detectedCount = 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lens Glint Finder",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
                                    .background(Color(0xFF9AA0B4), shape = RoundedCornerShape(4.dp))
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

                IconButton(onClick = { /* Toggle flash */ }) {
                    Icon(
                        imageVector = Icons.Default.FlashlightOn,
                        contentDescription = "Flash",
                        tint = Color.White
                    )
                }
            }

            // Camera preview area - mostly black for lens glint
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
            ) {
                // Overlay status pill
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp),
                    color = Color(0x80000000),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "$detectedCount likely glints",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Floating "What to look for" card
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 72.dp)
                .clickable { expandedGuidance = !expandedGuidance },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A26)
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFF00D8FF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "What to look for",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00D8FF)
                    )
                    Icon(
                        imageVector = if (expandedGuidance) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF00D8FF)
                    )
                }

                if (expandedGuidance) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Keep the room as dark as possible — total darkness works best. Small, sharp bright spots that remain fixed while you sweep slowly are the main sign of a camera lens. Ignore large reflections that jump around on mirrors or glass.",
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tip: hold the phone at eye level and move it slowly left to right.",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF9AA0B4),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

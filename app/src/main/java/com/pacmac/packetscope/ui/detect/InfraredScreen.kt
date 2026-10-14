package com.pacmac.packetscope.ui.detect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun InfraredScreen(navController: NavController) {
    var isScanning by remember { mutableStateOf(false) }
    var flashOn by remember { mutableStateOf(false) }
    var sensitivity by remember { mutableStateOf(0.5f) }
    var gaugeValue by remember { mutableStateOf(0f) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            while (isScanning) {
                delay(1000)
                elapsedSeconds++
                // Simulate IR detection with random values
                gaugeValue = (Random.nextFloat() * 30 * sensitivity).coerceIn(0f, 100f)
            }
        } else {
            elapsedSeconds = 0
            gaugeValue = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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

                if (isScanning) {
                    Surface(
                        color = Color(0xFFFF3B30).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF3B30)
                        )
                    }
                } else {
                    Surface(
                        color = Color(0xFF9AA0B4).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "IDLE",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9AA0B4)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Infrared Scan",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Detect IR LED reflections",
                fontSize = 14.sp,
                color = Color(0xFF9AA0B4)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Circular gauge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularGauge(
                    value = gaugeValue,
                    maxValue = 100f,
                    color = Color(0xFF8B5CF6)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Disclaimer
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFA726).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Phone camera can see some IR LEDs, not all hidden cameras.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = Color(0xFFFFA726)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Flash",
                    fontSize = 16.sp,
                    color = Color.White
                )
                Switch(
                    checked = flashOn,
                    onCheckedChange = { flashOn = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00D8FF),
                        checkedTrackColor = Color(0xFF00D8FF).copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sensitivity: ${(sensitivity * 100).toInt()}%",
                fontSize = 16.sp,
                color = Color.White
            )

            Slider(
                value = sensitivity,
                onValueChange = { sensitivity = it },
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF8B5CF6),
                    activeTrackColor = Color(0xFF8B5CF6)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Prompt or button
            if (!isScanning) {
                Text(
                    text = "Tap Start to begin scanning for IR reflections.",
                    fontSize = 14.sp,
                    color = Color(0xFF9AA0B4),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = { isScanning = !isScanning },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isScanning) Color(0xFFFF3B30) else Color(0xFF00D8FF)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (isScanning) "Stop" else "Start",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isScanning) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
private fun CircularGauge(
    value: Float,
    maxValue: Float,
    color: Color
) {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 20f
            val radius = (size.minDimension - strokeWidth) / 2

            // Background arc
            drawArc(
                color = Color(0xFF1A1A26),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )

            // Value arc
            val sweepAngle = (value / maxValue) * 270f
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${value.toInt()}%",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = when {
                    value < 20 -> "Low"
                    value < 60 -> "Medium"
                    else -> "High"
                },
                fontSize = 16.sp,
                color = Color(0xFF9AA0B4)
            )
        }
    }
}

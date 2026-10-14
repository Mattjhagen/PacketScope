package com.pacmac.packetscope.ui.detect

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.math.sqrt

@Composable
fun MagneticScreen(navController: NavController) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }
    var magneticValue by remember { mutableStateOf(0f) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    DisposableEffect(isScanning) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isScanning) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    magneticValue = sqrt(x * x + y * y + z * z)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        if (isScanning && magneticSensor != null) {
            sensorManager.registerListener(
                listener,
                magneticSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            while (isScanning) {
                delay(1000)
                elapsedSeconds++
            }
        } else {
            elapsedSeconds = 0
            magneticValue = 0f
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
                text = "Magnetic Sweep",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Detect magnetic fields",
                fontSize = 14.sp,
                color = Color(0xFF9AA0B4)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Needle gauge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                NeedleGauge(
                    value = magneticValue,
                    maxValue = 200f,
                    color = Color(0xFF2ECC71)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Numeric reading
            Text(
                text = String.format("%.1f µT", magneticValue),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2ECC71),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

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
                    text = "Magnetometer is approximate and affected by nearby metal / electronics.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = Color(0xFFFFA726)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Guidance
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A26)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Slowly sweep phone near surfaces. A sustained spike can indicate a magnet / mount. Check the spot visually.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    color = Color(0xFF9AA0B4),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Hold-to-scan button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { isScanning = true },
                            onDragEnd = { isScanning = false },
                            onDragCancel = { isScanning = false },
                            onDrag = { _, _ -> }
                        )
                    },
                color = if (isScanning) Color(0xFF2ECC71) else Color(0xFF1A1A26),
                shape = CircleShape
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isScanning) "Scanning..." else "Hold to Scan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isScanning) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun NeedleGauge(
    value: Float,
    maxValue: Float,
    color: Color
) {
    Canvas(modifier = Modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 - 20

        // Background arc
        drawArc(
            color = Color(0xFF1A1A26),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = 20f),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )

        // Value arc
        val sweepAngle = (value / maxValue) * 180f
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = sweepAngle.coerceIn(0f, 180f),
            useCenter = false,
            style = Stroke(width = 20f),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )

        // Needle
        val angle = Math.toRadians((180 + sweepAngle.coerceIn(0f, 180f)).toDouble())
        val needleLength = radius * 0.8f
        val needleEnd = Offset(
            center.x + (needleLength * kotlin.math.cos(angle)).toFloat(),
            center.y + (needleLength * kotlin.math.sin(angle)).toFloat()
        )

        drawLine(
            color = Color.White,
            start = center,
            end = needleEnd,
            strokeWidth = 4f
        )

        // Labels
        drawCircle(
            color = Color.White,
            radius = 8f,
            center = center
        )
    }
}

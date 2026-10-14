package com.pacmac.packetscope.ui.detect

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.sqrt

@Composable
fun MagneticScreen(navController: NavController) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }
    var magneticValue by remember { mutableStateOf(42.7f) }
    var expandedGuidance by remember { mutableStateOf(true) }

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
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Magnetic Sweep",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9AA0B4)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // What to look for card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedGuidance = !expandedGuidance },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A26)
            ),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00D8FF))
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
                        text = "A sudden jump in the reading near an object — compared to open air — can mean electronics or a speaker inside. Common hiding spots: smoke detectors, USB chargers, clocks, and picture frames.",
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tip: tap Calibrate here in an open area, then sweep slowly along walls and furniture.",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF9AA0B4),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Large reading
        Text(
            text = String.format("%.1f µT", magneticValue),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Gradient bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF00D8FF), Color(0xFF8B5CF6))
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            // Marker dot
            val markerPosition = (magneticValue / 200f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(markerPosition)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterEnd)
                        .background(Color.White, shape = CircleShape)
                        .border(2.dp, Color(0xFF0B0B14), shape = CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Low/High labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Low",
                fontSize = 12.sp,
                color = Color(0xFF9AA0B4)
            )
            Text(
                text = "High",
                fontSize = 12.sp,
                color = Color(0xFF9AA0B4)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Calibrate button
        Button(
            onClick = { /* Calibrate */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00D8FF)
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Calibrate here",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

package com.pacmac.packetscope.ui.detect

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DetectToolsScreen(navController: NavController) {
    var warningExpanded by remember { mutableStateOf(false) }
    var guidanceExpanded by remember { mutableStateOf(false) }
    var tipsExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
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
                    text = "Hidden Camera Detector",
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

        // Warning banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { warningExpanded = !warningExpanded },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFA726).copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFFFA726).copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Detection limitations",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA726),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (warningExpanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFFFA726)
                )
            }

            if (warningExpanded) {
                Text(
                    text = "Phone checks can help spot some cameras, not all. RF alone cannot confirm a hidden camera.",
                    fontSize = 14.sp,
                    color = Color(0xFFFFA726),
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 52.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section header
        Text(
            text = "What to look for",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool list (vertical)
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ToolListItem(
                icon = Icons.Default.Visibility,
                title = "Infrared Scan",
                description = "Find night-vision IR LEDs using the front camera",
                onClick = { navController.navigate("infrared") }
            )
            ToolListItem(
                icon = Icons.Default.FlashlightOn,
                title = "Lens Glint Finder",
                description = "Sweep with flashlight to spot camera lenses",
                onClick = { navController.navigate("lens_glint") }
            )
            ToolListItem(
                icon = Icons.Default.Explore,
                title = "Magnetic Sweep",
                description = "Detect magnetic fields from hidden electronics",
                onClick = { navController.navigate("magnetic") }
            )
            ToolListItem(
                icon = Icons.Default.WifiFind,
                title = "RF Network Scan",
                description = "Scan Wi-Fi and Bluetooth for networked cameras",
                onClick = { navController.navigate("scan") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Physical inspection tips
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { tipsExpanded = !tipsExpanded },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A26)
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFF262636))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Physical inspection tips",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Icon(
                    imageVector = if (tipsExpanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF9AA0B4)
                )
            }

            if (tipsExpanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    PhysicalTip("Check smoke detectors and air vents for unusual devices")
                    PhysicalTip("Inspect mirrors for two-way glass (fingernail test)")
                    PhysicalTip("Look for unusual holes in walls or decorations")
                    PhysicalTip("Examine power outlets and light fixtures")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ToolListItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A26)
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFF262636))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon in circle
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFF262636)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Title and description
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF9AA0B4),
                    maxLines = 1
                )
            }

            // Chevron
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF9AA0B4),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun PhysicalTip(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "•",
            color = Color(0xFF00D8FF),
            fontSize = 14.sp
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF9AA0B4),
            lineHeight = 20.sp
        )
    }
}

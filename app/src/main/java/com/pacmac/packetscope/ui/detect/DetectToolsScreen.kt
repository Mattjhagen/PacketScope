package com.pacmac.packetscope.ui.detect

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
    var guidanceExpanded by remember { mutableStateOf(true) }

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
            Column {
                Text(
                    text = "Detection Tools",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Check for hidden devices",
                    fontSize = 14.sp,
                    color = Color(0xFF9AA0B4)
                )
            }

            Surface(
                color = Color(0xFF9AA0B4).copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "IDLE",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9AA0B4)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Warning banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFA726).copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Phone checks can help spot some cameras, not all. RF alone cannot confirm a hidden camera.",
                    fontSize = 14.sp,
                    color = Color(0xFFFFA726),
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Collapsible guidance
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { guidanceExpanded = !guidanceExpanded },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A26)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "How to use",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = if (guidanceExpanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF9AA0B4)
                    )
                }

                if (guidanceExpanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "1. Start with the Network scan to check WiFi devices\n" +
                                "2. Use Infrared scan in dark rooms\n" +
                                "3. Try Magnetic sweep near suspected areas\n" +
                                "4. Lens Glint check with flashlight enabled",
                        fontSize = 14.sp,
                        color = Color(0xFF9AA0B4),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tool list (vertical)
        Text(
            text = "What to look for",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ToolListItem(
                icon = Icons.Default.Star,
                title = "Infrared Scan",
                description = "Find night-vision IR LEDs using the front camera",
                onClick = { navController.navigate("infrared") }
            )
            ToolListItem(
                icon = Icons.Default.Star,
                title = "Lens Glint Finder",
                description = "Sweep with flashlight to spot camera lenses",
                onClick = { navController.navigate("lens_glint") }
            )
            ToolListItem(
                icon = Icons.Default.LocationOn,
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
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A26)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Physical Inspection Tips",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                PhysicalTip("Check smoke detectors and air vents for unusual devices")
                PhysicalTip("Inspect mirrors for two-way glass (fingernail test)")
                PhysicalTip("Look for unusual holes in walls or decorations")
                PhysicalTip("Examine power outlets and light fixtures")
            }
        }
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
        shape = RoundedCornerShape(20.dp)
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

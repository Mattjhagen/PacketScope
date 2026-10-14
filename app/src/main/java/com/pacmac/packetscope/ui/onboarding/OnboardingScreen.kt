package com.pacmac.packetscope.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OnboardingFlow(
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedFrequency by remember { mutableStateOf<String?>(null) }
    var selectedExperience by remember { mutableStateOf<String?>(null) }
    var selectedUseCase by remember { mutableStateOf<String?>(null) }
    var selectedReferrals by remember { mutableStateOf(setOf<String>()) }

    when (currentStep) {
        0 -> OnboardingPage1(
            onNext = { currentStep = 1 },
            onSkip = { currentStep = 3 }
        )
        1 -> OnboardingPage2(
            onNext = { currentStep = 2 },
            onSkip = { currentStep = 3 }
        )
        2 -> SetupQuestion1(
            selected = selectedFrequency,
            onSelect = { selectedFrequency = it },
            onNext = { currentStep = 3 },
            onBack = { currentStep = 1 }
        )
        3 -> SetupQuestion2(
            selected = selectedExperience,
            onSelect = { selectedExperience = it },
            onNext = { currentStep = 4 },
            onBack = { currentStep = 2 }
        )
        4 -> SetupQuestion3(
            selected = selectedUseCase,
            onSelect = { selectedUseCase = it },
            onNext = { currentStep = 5 },
            onBack = { currentStep = 3 }
        )
        5 -> SetupQuestion4(
            selected = selectedReferrals,
            onToggle = { ref ->
                selectedReferrals = if (selectedReferrals.contains(ref)) {
                    selectedReferrals - ref
                } else {
                    selectedReferrals + ref
                }
            },
            onNext = { currentStep = 6 },
            onBack = { currentStep = 4 }
        )
        6 -> CalibrationScreen(
            onComplete = { onComplete() }
        )
    }
}

@Composable
private fun OnboardingPage1(
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    OnboardingPageLayout(
        graphic = { RadarGraphic() },
        heading = "Scan your surroundings",
        subtext = "See BLE and NFC signals around you, plus WiFi devices on your own network.",
        currentPage = 0,
        totalPages = 2,
        onNext = onNext,
        onSkip = onSkip
    )
}

@Composable
private fun OnboardingPage2(
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    OnboardingPageLayout(
        graphic = { MapGraphic() },
        heading = "Map what you find",
        subtext = "Track proximity and first-seen status for your own devices.",
        currentPage = 1,
        totalPages = 2,
        onNext = onNext,
        onSkip = onSkip
    )
}

@Composable
private fun OnboardingPageLayout(
    graphic: @Composable () -> Unit,
    heading: String,
    subtext: String,
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onSkip) {
                    Text("Skip", color = Color(0xFF9AA0B4))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Graphic
            Box(
                modifier = Modifier
                    .size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                graphic()
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Heading
            Text(
                text = heading,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtext
            Text(
                text = subtext,
                fontSize = 16.sp,
                color = Color(0xFF9AA0B4),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Progress dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(totalPages) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentPage) Color(0xFF00D8FF)
                                else Color(0xFF9AA0B4).copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue button
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D8FF)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun RadarGraphic() {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.minDimension / 2

        // Draw concentric circles
        for (i in 1..4) {
            val radius = maxRadius * (i / 4f)
            drawCircle(
                color = Color(0xFF00D8FF).copy(alpha = 0.1f),
                radius = radius,
                center = center,
                style = Stroke(width = 2f)
            )
        }

        // Draw sweeping arc
        drawArc(
            color = Color(0xFF00D8FF).copy(alpha = 0.3f),
            startAngle = angle - 30,
            sweepAngle = 30f,
            useCenter = true,
            topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
            size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
        )

        // Lock icon in center (simplified)
        drawCircle(
            color = Color(0xFF00D8FF),
            radius = 30f,
            center = center
        )
    }
}

@Composable
private fun MapGraphic() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)

        // Distance ring
        drawCircle(
            color = Color(0xFF00D8FF).copy(alpha = 0.15f),
            radius = 100f,
            center = center
        )

        // Scattered dots
        val dots = listOf(
            Offset(center.x + 50, center.y - 60),
            Offset(center.x - 40, center.y + 70),
            Offset(center.x + 80, center.y + 30),
            Offset(center.x - 70, center.y - 50),
            Offset(center.x + 30, center.y + 90)
        )

        dots.forEach { dot ->
            drawCircle(
                color = Color(0xFF8B5CF6),
                radius = 8f,
                center = dot
            )
        }

        // Center dot
        drawCircle(
            color = Color(0xFF00D8FF),
            radius = 12f,
            center = center
        )
    }
}

@Composable
private fun SetupQuestion1(
    selected: String?,
    onSelect: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    SetupQuestionLayout(
        progress = 0.25f,
        title = "How often do you check surroundings?",
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selected != null
    ) {
        SelectionCards(
            options = listOf("Daily", "Weekly", "Monthly", "Less often"),
            selected = selected,
            onSelect = onSelect,
            multiSelect = false
        )
    }
}

@Composable
private fun SetupQuestion2(
    selected: String?,
    onSelect: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    SetupQuestionLayout(
        progress = 0.5f,
        title = "Experience level?",
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selected != null
    ) {
        SelectionCards(
            options = listOf("Novice", "Some experience", "Expert"),
            selected = selected,
            onSelect = onSelect,
            multiSelect = false
        )
    }
}

@Composable
private fun SetupQuestion3(
    selected: String?,
    onSelect: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    SetupQuestionLayout(
        progress = 0.75f,
        title = "Main use case?",
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selected != null
    ) {
        SelectionCards(
            options = listOf("Home check", "Travel / hotel", "Office", "Other"),
            selected = selected,
            onSelect = onSelect,
            multiSelect = false
        )
    }
}

@Composable
private fun SetupQuestion4(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    SetupQuestionLayout(
        progress = 1f,
        title = "How did you hear about us?",
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selected.isNotEmpty()
    ) {
        MultiSelectionCards(
            options = listOf("Search", "YouTube", "Friend", "Social", "Store", "Other"),
            selected = selected,
            onToggle = onToggle
        )
    }
}

@Composable
private fun SetupQuestionLayout(
    progress: Float,
    title: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color(0xFF00D8FF),
                trackColor = Color(0xFF9AA0B4).copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Content
            content()

            Spacer(modifier = Modifier.weight(1f))

            // Continue button
            Button(
                onClick = onNext,
                enabled = nextEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D8FF),
                    disabledContainerColor = Color(0xFF9AA0B4).copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (nextEnabled) Color.Black else Color(0xFF9AA0B4)
                )
            }
        }
    }
}

@Composable
private fun SelectionCards(
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
    multiSelect: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { option ->
            val isSelected = selected == option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) Color(0xFF00D8FF) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(option) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        Color(0xFF00D8FF).copy(alpha = 0.1f)
                    else
                        Color(0xFF1A1A26)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF00D8FF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiSelectionCards(
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { option ->
            val isSelected = selected.contains(option)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) Color(0xFF00D8FF) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onToggle(option) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        Color(0xFF00D8FF).copy(alpha = 0.1f)
                    else
                        Color(0xFF1A1A26)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF00D8FF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalibrationScreen(
    onComplete: () -> Unit
) {
    var progress by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        for (i in 0..100 step 5) {
            progress = i
            delay(40)
        }
        delay(500)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B14)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pulsing radar
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                RadarGraphic()
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Progress percentage
            Text(
                text = "$progress%",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00D8FF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Matching your setup",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Running a quick first-run check.",
                fontSize = 14.sp,
                color = Color(0xFF9AA0B4)
            )
        }
    }
}

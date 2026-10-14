package com.pacmac.packetscope.ui.detect

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun InfraredScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var expandedGuidance by remember { mutableStateOf(true) }
    var torchEnabled by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var cameraControl by remember { mutableStateOf<androidx.camera.core.CameraControl?>(null) }

    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    var permissionRequested by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionRequested = true
        if (!granted) {
            cameraError = "Camera permission denied"
        }
    }

    val detectedCount = 2

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
                        text = "Infrared Scan",
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

                IconButton(
                    onClick = {
                        if (hasCameraPermission && cameraControl != null) {
                            torchEnabled = !torchEnabled
                            cameraControl?.enableTorch(torchEnabled)
                        }
                    },
                    enabled = hasCameraPermission && cameraControl != null
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashlightOn,
                        contentDescription = "Flash",
                        tint = if (torchEnabled) Color(0xFF00D8FF) else Color.White
                    )
                }
            }

            // Camera preview area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    !hasCameraPermission -> {
                        // Permission not granted - show request UI
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF1A1A26)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Camera permission required",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Grant camera access to detect infrared signals",
                                fontSize = 14.sp,
                                color = Color(0xFF9AA0B4)
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00D8FF)
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = "Allow Camera",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            if (permissionRequested && cameraError != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                        }
                                        context.startActivity(intent)
                                    },
                                    border = BorderStroke(1.dp, Color(0xFF3A3A4A)),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text(
                                        text = "Open Settings",
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                    cameraError != null -> {
                        // Camera error - show error UI
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF1A1A26)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Camera failed to start",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF3B30)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cameraError ?: "Unknown error",
                                fontSize = 14.sp,
                                color = Color(0xFF9AA0B4)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { cameraError = null },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00D8FF)
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(
                                    text = "Retry",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                    else -> {
                        // Camera preview
                        val previewView = remember { PreviewView(context) }

                        LaunchedEffect(Unit) {
                            try {
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                                val cameraProvider = cameraProviderFuture.get()

                                cameraProvider.unbindAll()

                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                val camera = cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview
                                )

                                cameraControl = camera.cameraControl
                            } catch (e: Exception) {
                                cameraError = e.message ?: "Failed to initialize camera"
                            }
                        }

                        DisposableEffect(Unit) {
                            previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
                            onDispose {
                                try {
                                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                                    cameraProviderFuture.get().unbindAll()
                                } catch (e: Exception) {
                                    // Ignore cleanup errors
                                }
                            }
                        }

                        AndroidView(
                            factory = { previewView },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Overlay status pill - only show when camera is active
                if (hasCameraPermission && cameraError == null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 24.dp),
                        color = Color(0x80000000),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "$detectedCount suspicious sources",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
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
                        text = "On Android this mode uses the rear camera. Keep the room as dark as possible — total darkness works best. Bright dots or glows on the preview, especially from dark corners, can be IR LEDs from night-vision cameras. They often appear when the room is actively recording.",
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tip: point the rear camera at a TV remote and press a button to confirm IR detection.",
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

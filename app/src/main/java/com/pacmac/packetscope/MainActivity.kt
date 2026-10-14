package com.pacmac.packetscope

import android.Manifest
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pacmac.packetscope.ui.screens.DetailScreen
import com.pacmac.packetscope.ui.screens.MyDevicesScreen
import com.pacmac.packetscope.ui.screens.ScanScreen
import com.pacmac.packetscope.ui.theme.PacketScopeTheme
import com.pacmac.packetscope.viewmodel.ScannerViewModel

class MainActivity : ComponentActivity() {

    private var showRationaleDialog by mutableStateOf(false)
    private var permissionsGranted by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted = permissions.all { it.value }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNfcAvailability()
        checkPermissions()

        setContent {
            PacketScopeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showRationaleDialog) {
                        PermissionRationaleDialog(
                            onDismiss = { showRationaleDialog = false },
                            onConfirm = {
                                showRationaleDialog = false
                                requestPermissions()
                            }
                        )
                    }

                    PacketScopeApp()
                }
            }
        }
    }

    private fun checkNfcAvailability() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            // Device doesn't support NFC
        }
    }

    private fun checkPermissions() {
        val requiredPermissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
            requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        val allGranted = requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted) {
            showRationaleDialog = true
        } else {
            permissionsGranted = true
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        permissionLauncher.launch(permissions.toTypedArray())
    }
}

@Composable
fun PermissionRationaleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.permission_rationale_title)) },
        text = { Text(stringResource(R.string.permission_rationale_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun PacketScopeApp() {
    val navController = rememberNavController()
    val viewModel: ScannerViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    icon = {},
                    label = { Text(stringResource(R.string.scan)) },
                    selected = false,
                    onClick = { navController.navigate("scan") }
                )
                NavigationBarItem(
                    icon = {},
                    label = { Text(stringResource(R.string.my_devices)) },
                    selected = false,
                    onClick = { navController.navigate("devices") }
                )
                NavigationBarItem(
                    icon = {},
                    label = { Text(stringResource(R.string.details)) },
                    selected = false,
                    onClick = { navController.navigate("details") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "scan",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("scan") {
                ScanScreen(viewModel)
            }
            composable("devices") {
                MyDevicesScreen()
            }
            composable("details") {
                DetailScreen()
            }
        }
    }
}

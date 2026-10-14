package com.pacmac.packetscope.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.pacmac.packetscope.viewmodel.PacketRecord

@Composable
fun DetailScreen(packet: PacketRecord?) {
    if (packet == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = "Packet not found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Packet Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val isNfc = packet.protocol == "NFC"

                DetailRow("Device Name", packet.deviceName)
                DetailRow(if (isNfc) "UID" else "MAC Address", packet.macAddress)
                if (!isNfc) {
                    DetailRow("RSSI", "${packet.rssi} dBm")
                    packet.txPower?.let { DetailRow("TX Power", "$it dBm") }
                }
                DetailRow("Protocol", packet.protocol)

                if (packet.serviceUuids.isNotEmpty()) {
                    DetailRow(
                        if (isNfc) "NDEF Records" else "Service UUIDs",
                        packet.serviceUuids.joinToString("\n")
                    )
                }

                if (packet.manufacturerData.isNotEmpty()) {
                    DetailRow(
                        if (isNfc) "Tag Info" else "Manufacturer Data",
                        packet.manufacturerData.entries.joinToString("\n") {
                            "${it.key}: ${it.value}"
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Raw bytes hex dump
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Raw Bytes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = packet.rawBytes.joinToString(" ") { byte ->
                        "%02X".format(byte)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

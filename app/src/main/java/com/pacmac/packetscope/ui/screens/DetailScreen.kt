package com.pacmac.packetscope.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.pacmac.packetscope.ui.components.HexDumpView
import com.pacmac.packetscope.viewmodel.PacketRecord
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailScreen(packet: PacketRecord?) {
    val context = LocalContext.current

    if (packet == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Packet not found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    val isNfc = packet.protocol == "NFC"
    val protocolColor = if (isNfc) Color(0xFF8B5CF6) else Color(0xFF00D8FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with protocol badge and share button
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
                    text = "Packet Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Surface(
                    color = protocolColor,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = packet.protocol,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = { sharePacket(context, packet) }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Basic info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("Device Name", packet.deviceName)
                DetailRow(if (isNfc) "UID" else "MAC Address", packet.macAddress)
                if (!isNfc) {
                    DetailRow("RSSI", "${packet.rssi} dBm")
                    packet.txPower?.let { DetailRow("TX Power", "$it dBm") }
                }
                DetailRow("Timestamp", formatTimestamp(packet.timestamp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Service UUIDs / NDEF Records
        if (packet.serviceUuids.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isNfc) "NDEF Records" else "Service UUIDs",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    packet.serviceUuids.forEach { item ->
                        Text(
                            text = "• $item",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Manufacturer Data / Tag Info
        if (packet.manufacturerData.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isNfc) "Tag Info" else "Manufacturer Data",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    packet.manufacturerData.entries.forEach { entry ->
                        Text(
                            text = "${entry.key}: ${entry.value}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Hex dump view
        HexDumpView(
            bytes = packet.rawBytes,
            protocol = packet.protocol
        )
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

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun sharePacket(context: android.content.Context, packet: PacketRecord) {
    try {
        val isNfc = packet.protocol == "NFC"
        val hexDump = buildHexDump(packet.rawBytes)

        val content = buildString {
            appendLine("PacketScope - ${packet.protocol} Packet")
            appendLine("=" .repeat(50))
            appendLine()
            appendLine("Device Name: ${packet.deviceName}")
            appendLine(if (isNfc) "UID: ${packet.macAddress}" else "MAC Address: ${packet.macAddress}")
            if (!isNfc) {
                appendLine("RSSI: ${packet.rssi} dBm")
                packet.txPower?.let { appendLine("TX Power: $it dBm") }
            }
            appendLine("Protocol: ${packet.protocol}")
            appendLine("Timestamp: ${formatTimestamp(packet.timestamp)}")
            appendLine()

            if (packet.serviceUuids.isNotEmpty()) {
                appendLine(if (isNfc) "NDEF Records:" else "Service UUIDs:")
                packet.serviceUuids.forEach { appendLine("  • $it") }
                appendLine()
            }

            if (packet.manufacturerData.isNotEmpty()) {
                appendLine(if (isNfc) "Tag Info:" else "Manufacturer Data:")
                packet.manufacturerData.entries.forEach {
                    appendLine("  ${it.key}: ${it.value}")
                }
                appendLine()
            }

            appendLine("Raw Bytes Hex Dump:")
            appendLine(hexDump)
        }

        val fileName = "packet_${packet.id.take(8)}_${System.currentTimeMillis()}.txt"
        val cacheDir = File(context.cacheDir, "shared")
        cacheDir.mkdirs()
        val file = File(cacheDir, fileName)
        file.writeText(content)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "PacketScope - ${packet.protocol} Packet")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share packet"))
    } catch (e: Exception) {
        // Silently fail
    }
}

private fun buildHexDump(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (row in 0 until (bytes.size + 15) / 16) {
        val offset = row * 16
        val rowBytes = bytes.sliceArray(offset until minOf(offset + 16, bytes.size))

        // Offset
        sb.append("%04X  ".format(offset))

        // Hex bytes
        for (i in 0 until 16) {
            if (i < rowBytes.size) {
                sb.append("%02X ".format(rowBytes[i]))
            } else {
                sb.append("   ")
            }
        }

        sb.append(" ")

        // ASCII
        rowBytes.forEach { byte ->
            val char = byte.toInt() and 0xFF
            sb.append(if (char in 32..126) char.toChar() else '.')
        }

        sb.appendLine()
    }
    return sb.toString()
}

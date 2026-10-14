package com.pacmac.packetscope.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HexDumpView(
    bytes: ByteArray,
    protocol: String = "BLE",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val asciiColor = if (protocol == "NFC") Color(0xFF8B5CF6) else Color(0xFF00D8FF)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A26)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Raw Bytes (${bytes.size} bytes)",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Button(
                    onClick = {
                        copyHexToClipboard(context, bytes)
                    }
                ) {
                    Text("Copy")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hex dump
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Column {
                    for (row in 0 until (bytes.size + 15) / 16) {
                        val offset = row * 16
                        val rowBytes = bytes.sliceArray(
                            offset until minOf(offset + 16, bytes.size)
                        )

                        HexDumpRow(
                            offset = offset,
                            bytes = rowBytes,
                            asciiColor = asciiColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HexDumpRow(
    offset: Int,
    bytes: ByteArray,
    asciiColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Offset column
        Text(
            text = "%04X".format(offset),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = Color(0xFF9AA0B4),
            modifier = Modifier.width(50.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Hex bytes column (16 bytes, padded if needed)
        Row(modifier = Modifier.width(400.dp)) {
            for (i in 0 until 16) {
                if (i < bytes.size) {
                    Text(
                        text = "%02X".format(bytes[i]),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "  ",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                if (i < 15) {
                    Text(
                        text = " ",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // ASCII column
        Text(
            text = bytes.toAsciiString(),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = asciiColor
        )
    }
}

private fun ByteArray.toAsciiString(): String {
    return String(
        this.map { byte ->
            val char = byte.toInt() and 0xFF
            if (char in 32..126) char.toChar() else '.'
        }.toCharArray()
    )
}

private fun copyHexToClipboard(context: Context, bytes: ByteArray) {
    val hexString = bytes.joinToString("") { "%02X".format(it) }
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Packet Hex", hexString)
    clipboard.setPrimaryClip(clip)
}

package com.pacmac.packetscope.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "scan_sessions")
data class ScanSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val type: String, // BLE, NFC, Magnetic, Infrared
    val count: Int,
    val locationLat: Double?,
    val locationLon: Double?
)

@Entity(
    tableName = "saved_packets",
    foreignKeys = [
        ForeignKey(
            entity = ScanSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class SavedPacket(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val protocol: String,
    val name: String,
    val identifier: String, // UID or MAC
    val rssi: Int,
    val timestamp: Long,
    val rawBytesHex: String,
    val isThreat: Boolean = false,
    val isMine: Boolean = false
)

@Entity(tableName = "my_devices")
data class MyDevice(
    @PrimaryKey
    val identifier: String,
    val name: String,
    val type: String,
    val addedTimestamp: Long,
    val note: String = ""
)

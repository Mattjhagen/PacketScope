package com.pacmac.packetscope.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanSessionDao {
    @Insert
    suspend fun insert(session: ScanSession): Long

    @Query("SELECT * FROM scan_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ScanSession>>

    @Query("SELECT * FROM scan_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): ScanSession?

    @Query("DELETE FROM scan_sessions WHERE timestamp < :cutoffTime")
    suspend fun deleteOlderThan(cutoffTime: Long)

    @Query("DELETE FROM scan_sessions")
    suspend fun deleteAll()
}

@Dao
interface SavedPacketDao {
    @Insert
    suspend fun insert(packet: SavedPacket)

    @Insert
    suspend fun insertAll(packets: List<SavedPacket>)

    @Query("SELECT * FROM saved_packets WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    fun getPacketsForSession(sessionId: Long): Flow<List<SavedPacket>>

    @Query("UPDATE saved_packets SET isThreat = :isThreat WHERE identifier = :identifier")
    suspend fun updateThreatStatus(identifier: String, isThreat: Boolean)

    @Query("UPDATE saved_packets SET isMine = :isMine WHERE identifier = :identifier")
    suspend fun updateMineStatus(identifier: String, isMine: Boolean)

    @Query("SELECT * FROM saved_packets WHERE isThreat = 1 ORDER BY timestamp DESC")
    fun getAllThreats(): Flow<List<SavedPacket>>

    @Query("DELETE FROM saved_packets")
    suspend fun deleteAll()
}

@Dao
interface MyDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: MyDevice)

    @Query("SELECT * FROM my_devices ORDER BY addedTimestamp DESC")
    fun getAllMyDevices(): Flow<List<MyDevice>>

    @Query("SELECT * FROM my_devices WHERE identifier = :identifier")
    suspend fun getDeviceByIdentifier(identifier: String): MyDevice?

    @Query("DELETE FROM my_devices WHERE identifier = :identifier")
    suspend fun delete(identifier: String)

    @Query("DELETE FROM my_devices")
    suspend fun deleteAll()
}

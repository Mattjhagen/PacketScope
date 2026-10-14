package com.pacmac.packetscope.data.local

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PacketRepository(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val scanSessionDao = database.scanSessionDao()
    private val savedPacketDao = database.savedPacketDao()
    private val myDeviceDao = database.myDeviceDao()

    init {
        // Auto-expire sessions older than 30 days on init
        CoroutineScope(Dispatchers.IO).launch {
            val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
            scanSessionDao.deleteOlderThan(thirtyDaysAgo)
        }
    }

    // Scan Sessions
    fun getAllSessions(): Flow<List<ScanSession>> = scanSessionDao.getAllSessions()

    suspend fun insertSession(session: ScanSession): Long = scanSessionDao.insert(session)

    suspend fun getSessionById(sessionId: Long): ScanSession? = scanSessionDao.getSessionById(sessionId)

    suspend fun deleteAllSessions() = scanSessionDao.deleteAll()

    // Saved Packets
    suspend fun insertPacket(packet: SavedPacket) = savedPacketDao.insert(packet)

    suspend fun insertPackets(packets: List<SavedPacket>) = savedPacketDao.insertAll(packets)

    fun getPacketsForSession(sessionId: Long): Flow<List<SavedPacket>> =
        savedPacketDao.getPacketsForSession(sessionId)

    suspend fun updateThreatStatus(identifier: String, isThreat: Boolean) =
        savedPacketDao.updateThreatStatus(identifier, isThreat)

    suspend fun updateMineStatus(identifier: String, isMine: Boolean) =
        savedPacketDao.updateMineStatus(identifier, isMine)

    fun getAllThreats(): Flow<List<SavedPacket>> = savedPacketDao.getAllThreats()

    suspend fun deleteAllPackets() = savedPacketDao.deleteAll()

    // My Devices
    suspend fun insertMyDevice(device: MyDevice) = myDeviceDao.insert(device)

    fun getAllMyDevices(): Flow<List<MyDevice>> = myDeviceDao.getAllMyDevices()

    suspend fun getMyDeviceByIdentifier(identifier: String): MyDevice? =
        myDeviceDao.getDeviceByIdentifier(identifier)

    suspend fun deleteMyDevice(identifier: String) = myDeviceDao.delete(identifier)

    suspend fun deleteAllMyDevices() = myDeviceDao.deleteAll()
}

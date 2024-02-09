package com.example.bletracker.data.repository

import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.Entry
import com.example.bletracker.data.source.network.model.Position
import com.example.bletracker.data.source.network.model.Tag
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import org.altbeacon.beacon.Beacon
import java.time.Instant

interface  LogRepository{
    suspend fun consumeLog() : Entries
    fun appendLog(entries: Collection<Beacon>)
    suspend fun appendLog(entries: List<Entry>)
}
class BLELogRepository ()  : LogRepository {

    // Mutex to make writes to cached values thread-safe.
    private val logMutex = Mutex()

    // Cache of the latest tag rssi and fields
    private var recentTagLog: MutableList<Entry> = mutableListOf()

    //provide service with copy of current log, clear log to start again
    override suspend fun consumeLog(): Entries {
        val logCopy = logMutex.withLock {
            val copy = recentTagLog.map { it.deepCopy() }
            recentTagLog.clear()
            copy
        }
        return Entries(logCopy)
    }

    //add to log
    override fun appendLog(beacons: Collection<Beacon>) {
        runBlocking {
            appendLog(beaconToEntry(beacons))
        }
    }

    override suspend fun appendLog(entries: List<Entry>) {
        logMutex.withLock {
            this.recentTagLog.addAll(entries)
        }
    }

    private fun beaconToEntry(beacons: Collection<Beacon>): List<Entry> {
        val entries: List<Entry> = beacons.map {
            Entry(
                //use first detection and average location for every beacon
                time = Instant.ofEpochMilli(it.firstCycleDetectionTimestamp)
                    .toKotlinInstant().toLocalDateTime(timeZone = TimeZone.currentSystemDefault()),
                tag = Tag(it.id3.toInt().toUShort(), it.id2.toInt().toUShort(), it.id1.toUuid()),
                //dist based on rssi running average at,
                distance = it.distance,
                //GPS not required atm
                position = Position(0.0, 0.0)
            )
        }
        return entries
    }
}

// TODO ADD COROUTINE SCOPE
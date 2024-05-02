package uk.ac.cam.smw98.bletracker.data.repository

import uk.ac.cam.smw98.bletracker.data.model.Entries
import uk.ac.cam.smw98.bletracker.data.model.Entry
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


interface  LogRepository{
    suspend fun consumeLog() : Entries
    suspend fun appendLog(entries: List<Entry>)
}
class BLELogRepository  : LogRepository {

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

    override suspend fun appendLog(entries: List<Entry>) {
        logMutex.withLock {
            this.recentTagLog.addAll(entries)
        }
    }

}


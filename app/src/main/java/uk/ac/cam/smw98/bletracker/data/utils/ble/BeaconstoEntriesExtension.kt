package uk.ac.cam.smw98.bletracker.data.utils.ble

import uk.ac.cam.smw98.bletracker.data.model.Entry
import uk.ac.cam.smw98.bletracker.data.model.Position
import uk.ac.cam.smw98.bletracker.data.model.Tag
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import org.altbeacon.beacon.Beacon
import java.time.Instant



fun Collection<Beacon>.toListEntry() : List<Entry> {
    val entries: List<Entry> = this.map {
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
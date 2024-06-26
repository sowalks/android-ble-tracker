package uk.ac.cam.smw98.bletracker.fake


import uk.ac.cam.smw98.bletracker.data.model.DeviceID
import uk.ac.cam.smw98.bletracker.data.model.Entries
import uk.ac.cam.smw98.bletracker.data.model.Entry
import uk.ac.cam.smw98.bletracker.data.model.LogStatus
import uk.ac.cam.smw98.bletracker.data.model.Position
import uk.ac.cam.smw98.bletracker.data.model.RegistrationFields
import uk.ac.cam.smw98.bletracker.data.model.Status
import uk.ac.cam.smw98.bletracker.data.model.Tag
import kotlinx.datetime.LocalDateTime
import java.util.UUID

object FakeDataSource {

    val deviceID = DeviceID(UUID(5L,5L))

    val locatorEntries= Entries(listOf(
        Entry(
            time = LocalDateTime(2024,12,14,9,55,0),
            tag =  Tag(),
            tagID = 1,
            distance =  3.0,
            position = Position(0.456,0.3456)
        ),
        Entry(
            time = LocalDateTime(2025,12,14,9,55,0),
            tag =  Tag(),
            tagID = 3,
            distance =  4.0,
            position = Position(0.456,0.3456)
        )
    )
    )
    val logEntries= Entries(listOf(
        Entry(
            time = LocalDateTime(2021,1,22,12,30,12),
            tag =  Tag(43U,1026U, UUID(654,2222)),
            tagID = 0,
            distance =  4.4,
            position = Position(52.19,0.56)
        ),
        Entry(
            time = LocalDateTime(2021,2, 21,9,20,11),
            tag =  Tag(1234U,12U, UUID(653,2245)),
            tagID = 0,
            distance =  4.4,
            position = Position(52.19,0.56)
        ),
        Entry(
            time = LocalDateTime(2011,2, 21,9,20,11),
            tag =  Tag(1234U,12U, UUID(653,2245)),
            tagID = 0,
            distance =  4.4,
            position = Position(52.19,0.56)
        )
    )
    )
    val  statusSuccess = Status(35)
    val  statusFail1 = Status(-1)
    val  statusFail12= Status(-2)

    val  logStatusSuccess = LogStatus(listOf(0,0,0))
    val  logStatusFail1 = LogStatus(listOf(0,-1,-1))
    val  logStatusFail12= LogStatus(listOf(-1,-1,-1))

    val  logStatusDuplicate1 = LogStatus(listOf(-2,-2,-2))
    val  logStatusDuplicate2=  LogStatus(listOf(0,-1,-2))

    val registrator = RegistrationFields(
        tag  =  Tag(43U,1026U, UUID(654,2222)),
        deviceId = UUID(5L,5L),
        mode = true
        )



}

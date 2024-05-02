package uk.ac.cam.smw98.bletracker.fake


import uk.ac.cam.smw98.bletracker.data.repository.NetworkRepository
import uk.ac.cam.smw98.bletracker.data.model.Entries
import uk.ac.cam.smw98.bletracker.data.model.Tag


class FakeNetworkRepository(): NetworkRepository {
    override suspend fun getLocations(): Entries {
        return FakeDataSource.locatorEntries
    }

    override suspend fun setMode(tagID: Int, mode: Boolean): Int {
       return if(mode){1}else{0}
    }

    override suspend fun submitLog(entries: Entries): List<Int> {
        return FakeDataSource.logStatusSuccess.status
    }

    override suspend fun registerTag(tag: Tag, mode: Boolean): Int {
        return FakeDataSource.statusSuccess.status
    }
}
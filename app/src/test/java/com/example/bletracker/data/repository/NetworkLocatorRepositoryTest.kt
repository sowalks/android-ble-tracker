package uk.ac.cam.smw98.bletracker.fake

import uk.ac.cam.smw98.bletracker.data.repository.NetworkLocatorRepository
import uk.ac.cam.smw98.bletracker.data.datasource.LocatorApiService

package uk.ac.cam.smw98.bletracker.data.repository

import uk.ac.cam.smw98.bletracker.rules.TestDispatcherRule
import uk.ac.cam.smw98.bletracker.ui.screens.MarsUiState
import uk.ac.cam.smw98.bletracker.ui.screens.MarsViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class NetworkLocatorRepositoryTest {

    @Test
    fun getLocations() {
    }

    @Test
    fun registerTag() {
    }

    @Test
    fun setMode() {
    }

    @Test
    fun submitLog() {}
    package uk.ac.cam.smw98.bletracker.fake

    import uk.ac.cam.smw98.bletracker.data.repository.NetworkLocatorRepository
    import junit.framework.TestCase.assertEquals
    import kotlinx.coroutines.test.runTest
    import org.junit.Test

    class NetworkLocatorRepositoryTest {
        @Test
        fun networkLocatorRepository_getLocations_verifyPhotoList() = runTest {

            val repository = NetworkLocatorRepository(
                trackerApiService = FakeLocatorApiService(),
                localDeviceIDRepository = FakeDeviceIDRepository()
            )
            assertEquals(FakeDataSource.locatorEntries, repository.getLocations())
            assertEquals(FakeDataSource.logStatusSuccess.status, repository.submitLog(FakeDataSource.logEntries))
            assertEquals(FakeDataSource.statusSuccess.status, repository.registerTag(FakeDataSource.registrator.tag,FakeDataSource.registrator.mode))
        }
        package uk.ac.cam.smw98.bletracker.fake

        import uk.ac.cam.smw98.bletracker.rules.TestDispatcherRule
        import uk.ac.cam.smw98.bletracker.ui.screens.MarsUiState
        import uk.ac.cam.smw98.bletracker.ui.screens.MarsViewModel
        import junit.framework.TestCase.assertEquals
        import kotlinx.coroutines.test.runTest
        import org.junit.Rule
        import org.junit.Test

        class MarsViewModelTest {
            @get:Rule
            val testDispatcher = TestDispatcherRule()
            @Test
            fun marsViewModel_getMarsPhotos_verifyMarsUiStateSuccess() =
                runTest{
                    val marsViewModel = MarsViewModel(
                        locatorRepository = FakeNetworkLocatorRepository()
                    )
                    assertEquals(
                        MarsUiState.Success("Success: ${FakeDataSource.locatorEntries.entries.size} Mars " +
                                "photos retrieved"),
                        marsViewModel.marsUiState
                    )

                }

        }
    }
    @ -1,6 +1,7 @@
    package uk.ac.cam.smw98.bletracker.fake
    package uk.ac.cam.smw98.bletracker.data.utils

    import uk.ac.cam.smw98.bletracker.data.utils.network.LocatorApiService
    import uk.ac.cam.smw98.bletracker.fake.FakeDataSource
    import uk.ac.cam.smw98.bletracker.rules.TestDispatcherRule
    import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
    import junit.framework.TestCase.assertEquals
    @ -36,13 +37,16 @@ class TrackerAPIServiceTest {
        .build()
        val retrofitTestService: LocatorApiService =   retrofit.create(LocatorApiService::class.java)
        val entries =  retrofitTestService.getLocations(FakeDataSource.deviceID).entries
        assertEquals("Entries Recieved should be ${FakeDataSource.locatorEntries.entries.size}, is ${entries.size} ",entries.size,FakeDataSource.locatorEntries.entries.size
        assertEquals("Entries Recieved should be ${FakeDataSource.locatorEntries.entries.size}, is ${entries.size} ",entries.size,
        FakeDataSource.locatorEntries.entries.size

        )
        assertEquals("Entries 0 tagID is ${entries[0].tagID}",entries[0].tagID,FakeDataSource.locatorEntries.entries[0].tagID
        assertEquals("Entries 0 tagID is ${entries[0].tagID}",entries[0].tagID,
        FakeDataSource.locatorEntries.entries[0].tagID

        )
        assertEquals(retrofitTestService.getLocations(FakeDataSource.deviceID).entries[1].tagID,FakeDataSource.locatorEntries.entries[1].tagID
        assertEquals(retrofitTestService.getLocations(FakeDataSource.deviceID).entries[1].tagID,
        FakeDataSource.locatorEntries.entries[1].tagID

        )
    }
    @ -55,12 +59,14 @@ class TrackerAPIServiceTest {
        .build()
        val retrofitTestService: LocatorApiService =   retrofit.create(LocatorApiService::class.java)
        val res =  retrofitTestService.submitLog(FakeDataSource.logEntries)
        assertEquals("Status Recieved should be ${FakeDataSource.logStatusDuplicate1}, is $res .",res,FakeDataSource.logStatusDuplicate1)
        assertEquals("Status Recieved should be ${FakeDataSource.logStatusDuplicate1}, is $res .",res,
        FakeDataSource.logStatusDuplicate1
        )

    }

    @Test
    fun  trackerApiService_verify_server_registrate() =
    fun  trackerApiService_verify_server_register() =
        runTest{
            val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            @ -68,7 +74,9 @@ fun  trackerApiService_verify_server_registrate() =
            .build()
            val retrofitTestService: LocatorApiService =   retrofit.create(LocatorApiService::class.java)
            val res =  retrofitTestService.registerTag(FakeDataSource.registrator)
            assertEquals("Status Recieved should be ${FakeDataSource.statusFail12}, is $res .",res,FakeDataSource.statusFail12)
            assertEquals("Status Recieved should be ${FakeDataSource.statusFail12}, is $res .",res,
                FakeDataSource.statusFail12
            )

        }
}


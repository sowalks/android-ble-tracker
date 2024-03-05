package com.example.bletracker.fake

import com.example.bletracker.data.source.network.LocatorApiService
import com.example.bletracker.rules.TestDispatcherRule
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit

class LocatorAPIServiceTest {
    private val baseURL =
        " http://127.0.0.1:5000"
//Simple test for now for duplicates and was successes.
    @get:Rule
    val testDispatcher = TestDispatcherRule()
    @Test
    fun  locatorApiService_verify_server_get_deviceID() =
        runTest{
             val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .baseUrl(baseURL)
                .build()
            val retrofitTestService: LocatorApiService =   retrofit.create(LocatorApiService::class.java)
            val id =  retrofitTestService.getDeviceID()
            assertTrue("Assert  get DeviceID returns a valid ID greater than 0 on server. It is ${id.deviceID}",
                id.deviceID > 0
            )

        }
    @Test
    fun  locatorApiService_verify_server_get_locations() =
        runTest{
            val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .baseUrl(baseURL)
                .build()
            val retrofitTestService: LocatorApiService =   retrofit.create(LocatorApiService::class.java)
            val entries =  retrofitTestService.getLocations(FakeDataSource.deviceID).entries
            assertEquals("Entries Recieved should be ${FakeDataSource.locatorEntries.entries.size}, is ${entries.size} ",entries.size,FakeDataSource.locatorEntries.entries.size

            )
            assertEquals("Entries 0 tagID is ${entries[0].tagID}",entries[0].tagID,FakeDataSource.locatorEntries.entries[0].tagID

            )
            assertEquals(retrofitTestService.getLocations(FakeDataSource.deviceID).entries[1].tagID,FakeDataSource.locatorEntries.entries[1].tagID

            )
        }
    @Test
    fun  locatorApiService_verify_server_submitlog() =
        runTest{
            val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .baseUrl(baseURL)
                .build()
            val retrofitTestService: LocatorApiService =   retrofit.create(LocatorApiService::class.java)
            val res =  retrofitTestService.submitLog(FakeDataSource.logEntries)
            assertEquals("Status Recieved should be ${FakeDataSource.logStatusDuplicate1}, is $res .",res,FakeDataSource.logStatusDuplicate1)

        }

@Test
fun  locatorApiService_verify_server_registrate() =
    runTest{
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(baseURL)
            .build()
        val retrofitTestService: LocatorApiService =   retrofit.create(LocatorApiService::class.java)
        val res =  retrofitTestService.registerTag(FakeDataSource.registrator)
        assertEquals("Status Recieved should be ${FakeDataSource.statusFail12}, is $res .",res,FakeDataSource.statusFail12)

    }
}

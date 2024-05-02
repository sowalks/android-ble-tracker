package uk.ac.cam.smw98.bletracker.data.utils

/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified from MarsPhotos Code Lab.
 */

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.location.LocationServices
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.altbeacon.beacon.Region
import retrofit2.Retrofit
import uk.ac.cam.smw98.bletracker.data.ProtoOwnedEntries
import uk.ac.cam.smw98.bletracker.data.datasource.OwnedTagsSource
import uk.ac.cam.smw98.bletracker.data.datasource.TrackerApiService
import uk.ac.cam.smw98.bletracker.data.model.OwnedTagsSerializer
import uk.ac.cam.smw98.bletracker.data.repository.BLELogRepository
import uk.ac.cam.smw98.bletracker.data.repository.DefaultDeviceIDRepository
import uk.ac.cam.smw98.bletracker.data.repository.LocalDeviceIDRepository
import uk.ac.cam.smw98.bletracker.data.repository.LocalOwnedTagsRepository
import uk.ac.cam.smw98.bletracker.data.repository.LocationFusedRepository
import uk.ac.cam.smw98.bletracker.data.repository.LocationRepository
import uk.ac.cam.smw98.bletracker.data.repository.LogRepository
import uk.ac.cam.smw98.bletracker.data.repository.NetworkLocatorRepository
import uk.ac.cam.smw98.bletracker.data.repository.OwnedTagsRepository
import uk.ac.cam.smw98.bletracker.data.utils.ble.BLEBeaconHelper
import uk.ac.cam.smw98.bletracker.data.utils.ble.BLEHelper


interface AppContainer {
    val networkLocatorRepository : NetworkLocatorRepository
    val region : Region
    val bleHelper: BLEBeaconHelper
    val logRepository : LogRepository
    val loggingPeriod : Long
    val smoothingPeriod: Long
    val locationRepository: LocationRepository
    val permissionManager: PermissionManager
    val ownedTagsRepository: OwnedTagsRepository
}


//only ever a single instance, so initialize in context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "device")

val Context.ownedEntriesStore: DataStore<ProtoOwnedEntries> by
dataStore(
    fileName =  "owned_tags.pb", serializer = OwnedTagsSerializer
)


class DefaultAppContainer(context : Context) : AppContainer {


    private val baseURL = "https://192.168.33.204:5000"


    //dev client to not have to worry ab self certified certificate
    private val okhttpClientDev = OkHttpClient.Builder()
        .hostnameVerifier { _, _ ->
            true
        }
        //logging to help solve issue with sent json not matching received json
        .addInterceptor(interceptor = HttpLoggingInterceptor().apply
        {
            this.level = HttpLoggingInterceptor.Level.BODY // BODY FOR FULL DETAILS
        })
        .build()

    //convert to json -  serializer defined. Use httpclient for ssl + baseurl of server
    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okhttpClientDev)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseURL)
        .build()

    private val retrofitService: TrackerApiService by lazy {
        //retrofit takes java classes only / gives equivalent of kotlin
        retrofit.create(TrackerApiService::class.java)
    }

    private val deviceIDRepository : LocalDeviceIDRepository by lazy {
        DefaultDeviceIDRepository(context.dataStore)
    }

    override val permissionManager: AppPermissionManager by lazy{
        AppPermissionManager(context=context)
    }


    private val  locationClient by lazy{  LocationServices.getFusedLocationProviderClient(context)}

    private val dataSourceTags by lazy { OwnedTagsSource(context.ownedEntriesStore)}

    override val ownedTagsRepository : OwnedTagsRepository by lazy {
        LocalOwnedTagsRepository(dataSourceTags)
    }

    override val locationRepository: LocationRepository by lazy {
        LocationFusedRepository(locationClient,permissionManager)
    }


    private  val betweenScansPeriod: Long = 0

    private val scanPeriod : Long = 1100L

    override val loggingPeriod: Long = 10000L

    override val smoothingPeriod: Long = 10000L

    override val bleHelper : BLEBeaconHelper by lazy {
        BLEHelper(context, logRepository, locationRepository, region, scanPeriod = scanPeriod,betweenScanPeriod=betweenScansPeriod,smoothingPeriod=smoothingPeriod)
    }

    override val logRepository : LogRepository by lazy{ BLELogRepository() }

    override val networkLocatorRepository: NetworkLocatorRepository by lazy {
        NetworkLocatorRepository(retrofitService,deviceIDRepository)
    }
    // the region definition ensures we are looking for any possible iBeacon
    override val region : Region by lazy {Region("all-beacons", null, null, null)}

}
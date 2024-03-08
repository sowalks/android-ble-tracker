package com.example.bletracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.bletracker.data.ble.BLEBeaconHelper
import com.example.bletracker.data.ble.BLEHelper
import com.example.bletracker.data.repository.AppPermissionManager
import com.example.bletracker.data.repository.BLELogRepository
import com.example.bletracker.data.repository.DefaultDeviceIDRepository
import com.example.bletracker.data.repository.LocalDeviceIDRepository
import com.example.bletracker.data.repository.LocationFusedRepository
import com.example.bletracker.data.repository.LocationRepository
import com.example.bletracker.data.repository.LogRepository
import com.example.bletracker.data.repository.NetworkLocatorRepository
import com.example.bletracker.data.repository.PermissionManager
import com.example.bletracker.data.source.network.LocatorApiService
import com.google.android.gms.location.LocationServices
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.altbeacon.beacon.Region
import retrofit2.Retrofit
import javax.net.ssl.HostnameVerifier



interface AppContainer {
    val networkLocatorRepository : NetworkLocatorRepository
    val region : Region
    val bleHelper: BLEBeaconHelper
    val logRepository : LogRepository
    val loggingPeriod : Long
    val smoothingPeriod: Long
    val locationRepository: LocationRepository
    val permissionManager: PermissionManager
}

//only ever a single instance, so initialize in context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "device")

class DefaultAppContainer(context : Context) : AppContainer {

    private val baseURL =
        "https://10.10.13.138:5000"

    //dev client to not have to worry ab self certified certificate
    private val okhttpClientDev = OkHttpClient.Builder()
        .hostnameVerifier(HostnameVerifier {_,_->
            true
        })
        .build()
    //convert to json -  serializer defined. Use httpclient for ssl + baseurl of server
    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okhttpClientDev)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseURL)
        .build()

    private val retrofitService: LocatorApiService by lazy {
        //retrofit takes java classes only / gives equivalent of kotlin
        retrofit.create(LocatorApiService::class.java)
    }

    private val deviceIDRepository : LocalDeviceIDRepository by lazy {
        DefaultDeviceIDRepository(context.dataStore)
    }

    override val permissionManager: AppPermissionManager by lazy{
        AppPermissionManager(context=context)
    }


    private val  locationClient by lazy{  LocationServices.getFusedLocationProviderClient(context)}



    override val locationRepository: LocationRepository by lazy {
        LocationFusedRepository(locationClient,permissionManager)
    }


    private  val betweenScansPeriod: Long = 0

    private val scanPeriod : Long = 1100L

    override val loggingPeriod: Long = 10000L

    override val smoothingPeriod: Long = 10000L

    override val bleHelper :BLEBeaconHelper by lazy {
        BLEHelper(context, logRepository, locationRepository, region, scanPeriod = scanPeriod,betweenScanPeriod=betweenScansPeriod,smoothingPeriod=smoothingPeriod)
    }

    override val logRepository : LogRepository by lazy{ BLELogRepository() }

    override val networkLocatorRepository: NetworkLocatorRepository by lazy {
        NetworkLocatorRepository(retrofitService,deviceIDRepository)
    }
    // the region definition ensures we are looking for any possible iBeacon
    override val region : Region by lazy {Region("all-beacons", null, null, null)}

}
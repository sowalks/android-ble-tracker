package com.example.bletracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.bletracker.data.repository.DefaultDeviceIDRepository
import com.example.bletracker.data.repository.LocalDeviceIDRepository
import com.example.bletracker.data.repository.LocatorRepository
import com.example.bletracker.data.repository.NetworkLocatorRepository
import retrofit2.Retrofit
import com.example.bletracker.data.source.network.LocatorApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType


interface AppContainer {
    val locatorRepository : LocatorRepository
}

//only ever a single instance, so initialize in context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "device")

class DefaultAppContainer(context : Context) : AppContainer {

    private val baseURL =
        "http://127.0.0.1:5000"

    //convert to json -  serializer defined. Use httpclient for ssl + baseurl of server
    private val retrofit: Retrofit = Retrofit.Builder()
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

    override val locatorRepository: LocatorRepository by lazy {
        NetworkLocatorRepository(retrofitService,deviceIDRepository)
    }

}
package com.example.bletracker.data.source.network
import com.example.bletracker.data.source.network.model.DeviceID
import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.LogStatus
import com.example.bletracker.data.source.network.model.Status
import com.example.bletracker.data.source.network.model.Registrator
import com.example.bletracker.data.source.network.model.SetModeBody
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.PUT


interface  LocatorApiService {
    @GET("device")
    //device id generate
    suspend fun getDeviceID() : DeviceID
    @POST("locations")
    //Get locations of device's tags
    suspend fun getLocations(@Body deviceID : DeviceID) : Entries
    @POST("log")
    // log locations and time tag detected
    suspend fun submitLog(@Body entries : Entries) : LogStatus
    @POST("registration")
    // register tag to device
    suspend fun registerTag(@Body register : Registrator) : Status

    @PUT("set-mode")
    suspend fun setMode(@Body setModeBody: SetModeBody) : Status
}


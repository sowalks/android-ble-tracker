package com.example.bletracker.data.datasource
import com.example.bletracker.data.model.DeviceID
import com.example.bletracker.data.model.Entries
import com.example.bletracker.data.model.LogStatus
import com.example.bletracker.data.model.RegistrationFields
import com.example.bletracker.data.model.SetModeBody
import com.example.bletracker.data.model.Status
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface  LocatorApiService {
    @POST("device")
    //device id generate
    suspend fun getDeviceID() : DeviceID

    @GET("locations/{deviceID}")
    //Get locations of device's tags
    suspend fun getLocations(@Path("deviceID") deviceID : DeviceID) : Entries

    @POST("log")
    // log locations and time tag detected
    suspend fun submitLog(@Body entries : Entries) : LogStatus

    @POST("registration")
    // register tag to device
    suspend fun registerTag(@Body register : RegistrationFields) : Status

    @PUT("mode/{tagID}")
    suspend fun setMode(@Path("tagID") tagID: Int, @Body setModeBody: SetModeBody) : Status
}


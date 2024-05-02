package uk.ac.cam.smw98.bletracker.data.datasource
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import uk.ac.cam.smw98.bletracker.data.model.DeviceID
import uk.ac.cam.smw98.bletracker.data.model.Entries
import uk.ac.cam.smw98.bletracker.data.model.LogStatus
import uk.ac.cam.smw98.bletracker.data.model.RegistrationFields
import uk.ac.cam.smw98.bletracker.data.model.SetModeBody
import uk.ac.cam.smw98.bletracker.data.model.Status


interface  TrackerApiService {
    @POST("device")
    //device id generate
    suspend fun getDeviceID() : DeviceID

    @GET("locations/{deviceID}")
    //Get locations of device's tags
    suspend fun getLocations(@Path("deviceID") deviceID : String) : Entries

    @POST("log")
    // log locations and time tag detected
    suspend fun submitLog(@Body entries : Entries) : LogStatus

    @POST("registration")
    // register tag to device
    suspend fun registerTag(@Body register : RegistrationFields) : Status

    @PUT("mode/{tagID}")
    suspend fun setMode(@Path("tagID") tagID: Int, @Body setModeBody: SetModeBody) : Status
}


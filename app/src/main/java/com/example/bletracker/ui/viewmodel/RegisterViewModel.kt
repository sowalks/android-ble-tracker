package com.example.bletracker.ui.viewmodel



import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bletracker.BackgroundApplication
import com.example.bletracker.data.model.Tag
import com.example.bletracker.data.model.UpdateUiState
import com.example.bletracker.data.repository.NetworkRepository
import com.example.bletracker.data.utils.ble.BeaconRangingSmoother
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import retrofit2.HttpException
import java.io.IOException




class RegisterViewModel(val locatorRepository:NetworkRepository, private val smoothingPeriod: Long = 10000) : ViewModel(){

// uiState is used to update screen, but private set to only be modified here
var registerUiState: UpdateUiState by mutableStateOf(UpdateUiState.Idle)
        private set
private val beaconSmoother : BeaconRangingSmoother =  BeaconRangingSmoother(smoothingPeriod)

fun registerTag(tag: Tag) {
        viewModelScope.launch{
                registerUiState = UpdateUiState.Loading
                Log.d(TAG, " Register Loading")
                registerUiState = try {
                        when (val status = locatorRepository.registerTag(tag=tag,mode=true)) {
                            -2 -> { Log.d(TAG, "Already Registered")
                                    UpdateUiState.Error("Register Failed")}
                            -1 -> { Log.d(TAG, "Server failed Register")
                                    UpdateUiState.Error("Register Failed, Try again.")}
                            else -> { Log.d(TAG, "Success")
                                    UpdateUiState.Success(status)}
                        }
                }
                catch(e : IOException){
                        Log.d(TAG, " Register IO Error")
                        UpdateUiState.Error(e.toString())
                }
                catch(e : HttpException){
                        Log.d(TAG, " Register HTTP Error")
                        UpdateUiState.Error(e.message())
                }
        }

}
        fun userNotified(){
                registerUiState = UpdateUiState.Idle
        }
        fun smoothBeacons(beacons:Collection<Beacon>) : Collection<Beacon>
        {
                return beaconSmoother.add(beacons).visibleBeacons
        }
companion object{
        const val TAG = "RegisterViewModel"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
                initializer {
                        val application = (this[APPLICATION_KEY] as BackgroundApplication)
                        val locatorRepository = application.container.networkLocatorRepository
                        val smoothingPeriod = application.container.smoothingPeriod
                        RegisterViewModel(locatorRepository = locatorRepository,smoothingPeriod=smoothingPeriod)
                }
        }
}
}

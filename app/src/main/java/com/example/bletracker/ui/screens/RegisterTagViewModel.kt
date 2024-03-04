package com.example.bletracker.ui.screens



import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bletracker.BeaconReferenceApplication
import com.example.bletracker.data.ble.BeaconRangingSmoother
import com.example.bletracker.data.repository.LocatorRepository
import com.example.bletracker.data.source.network.model.DeviceID
import com.example.bletracker.data.source.network.model.Tag
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import retrofit2.HttpException
import java.io.IOException

sealed interface  RegisterUiState {
        data class Success(val tagID : Int) : RegisterUiState
        data class Error(val msg: String) : RegisterUiState
        object Idle : RegisterUiState
        object Loading: RegisterUiState
}


class RegisterTagViewModel(val locatorRepository: LocatorRepository) : ViewModel(){

// uiState is used to update screen, but private set to only be modified here
var registerUiState: RegisterUiState by mutableStateOf(RegisterUiState.Idle)
        private set


fun registerTag(tag:Tag) {
        viewModelScope.launch{
                registerUiState = RegisterUiState.Loading
                Log.d(TAG, " Register Loading")
                registerUiState = try {
                        val status = locatorRepository.registerTag(tag=tag,mode=true)
                        when{
                                status == -2 -> { Log.d(TAG, "Already Registered")
                                        RegisterUiState.Error("Register Failed")}
                                status == -1 ->   { Log.d(TAG, "Server failed Register")
                                        RegisterUiState.Error("Register Failed, Try again.")}
                                else ->  { Log.d(TAG, "Success")
                                        RegisterUiState.Success(status)}

                        }
                }
                catch(e : IOException){
                        Log.d(TAG, " Register IO Error")
                        RegisterUiState.Error(e.toString())
                }
                catch(e : HttpException){
                        Log.d(TAG, " Register HTTP Error")
                        RegisterUiState.Error(e.message())
                }
        }

}
        fun userNotified(){
                registerUiState = RegisterUiState.Idle
        }
        fun smoothBeacons(beacons:Collection<Beacon>) : Collection<Beacon>
        {
                return BeaconRangingSmoother.shared.add(beacons).visibleBeacons
        }
companion object{
        val TAG = "RegisterViewModel"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
                initializer {
                        val application = (this[APPLICATION_KEY] as BeaconReferenceApplication)
                        val locatorRepository = application.container.locatorRepository
                        RegisterTagViewModel(locatorRepository = locatorRepository)
                }
        }
}
}

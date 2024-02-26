package com.example.bletracker.ui.screens



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bletracker.MarsPhotosApplication
import com.example.bletracker.data.repository.LocatorRepository
import com.example.bletracker.data.source.network.model.Tag
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface  RegisterUiState {
        data class Success(val tagID : Int) : RegisterUiState
        data class Error(val msg: String) : RegisterUiState
        object Loading: RegisterUiState
}


class RegisterTagViewModel(val locatorRepository: LocatorRepository) : ViewModel(){

// uiState is used to update screen, but private set to only be modified here
var registerUiState: RegisterUiState by mutableStateOf(RegisterUiState.Loading)
        private set


fun registerTag(tag:Tag) {
        viewModelScope.launch{
                registerUiState = RegisterUiState.Loading
                registerUiState = try {
                        RegisterUiState.Success(locatorRepository.registerTag(tag=tag,mode=true))}
                catch(e : IOException){
                        RegisterUiState.Error(e.toString())
                }
                catch(e : HttpException){
                        RegisterUiState.Error(e.message())
                }
        }
}


companion object {
                val Factory: ViewModelProvider.Factory = viewModelFactory {
                        initializer {
                                val application = (this[APPLICATION_KEY] as MarsPhotosApplication)
                                val locatorRepository = application.container.locatorRepository
                                RegisterTagViewModel(locatorRepository = locatorRepository)
                        }
                }
        }
}

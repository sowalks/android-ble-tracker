package com.example.bletracker
import android.app.Application
import com.example.bletracker.data.AppContainer
import com.example.bletracker.data.DefaultAppContainer



class MarsPhotosApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }

}
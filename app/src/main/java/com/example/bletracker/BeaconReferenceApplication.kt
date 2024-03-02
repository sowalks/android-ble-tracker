package com.example.bletracker

import android.app.Application

import android.util.Log
import com.example.bletracker.data.AppContainer
import com.example.bletracker.data.DefaultAppContainer
import com.example.bletracker.data.ble.BLEHelper
import com.example.bletracker.data.repository.BLELogRepository
import com.example.bletracker.data.repository.LocatorRepository
import com.example.bletracker.data.repository.LogRepository
import com.example.bletracker.data.repository.NetworkLocatorRepository
import kotlinx.coroutines.runBlocking
import org.altbeacon.beacon.Region
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BeaconReferenceApplication : Application() {
    lateinit var container: AppContainer
    val executor: ExecutorService = Executors.newFixedThreadPool(1)

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
        //Separate thread required to execute periodic requests
        // more regularly than ~15 minutes w/ Android killing it
        executor.execute(Runnable{
            while (true) {
                val beacons = runBlocking {
                   container.logRepository.consumeLog()
                }
                val success = runBlocking {
                    container.locatorRepository.submitLog(beacons)
                }
                if(success.contains(-1)){
                    Log.d(TAG, "Error in submitting log of ${success.size} beacons")
                }
                Log.d(TAG, "I will log this line every $LOGGING_PERIOD forever")
                Thread.sleep(LOGGING_PERIOD)
                // FileOutputStream( "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/log=_10sec.csv",true ).writeCsv(beacons)
            }
        })

        setupBeaconScanning()
    }

    fun setupBeaconScanning() {
        return container.bleHelper.setupBeaconScanning()
    }

    companion object {
        const val TAG = "BeaconReference"
        const val  LOGGING_PERIOD = 10000L
    }

}
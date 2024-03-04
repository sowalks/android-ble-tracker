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
import com.example.bletracker.ui.screens.LocateViewModel
import com.example.bletracker.ui.screens.LocatorUiState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.altbeacon.beacon.Region
import retrofit2.HttpException
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BeaconReferenceApplication : Application() {
    lateinit var container: AppContainer
    private val executor: ExecutorService = Executors.newFixedThreadPool(1)

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
        //Separate thread required to execute periodic requests
        // more regularly than ~15 minutes w/ Android killing it
        executor.execute(Runnable{
            while (true) {
                    val beacons =
                        runBlocking {
                            container.logRepository.consumeLog()
                        }
                    if(beacons.entries.isNotEmpty()) {
                        val success =
                            runBlocking {
                                try {
                                    container.locatorRepository.submitLog(beacons)
                                } catch (e: IOException) {
                                    Log.d(TAG, e.toString())
                                    listOf(-2)
                                } catch (e: HttpException) {
                                    Log.d(TAG, e.message())
                                    listOf(-2)
                                }
                            }


                        if (success.contains(-1)) {
                            Log.d(TAG, "Error in submitting log of ${success.size} beacons")
                        } else if (success.size == 1 && success[0] == -2) {
                            Log.d(TAG, "Connection Error on ${beacons.entries.size}")
                        }
                    }
                Log.d(TAG, "I will log this line every $LOGGING_PERIOD forever")
                Thread.sleep(LOGGING_PERIOD)
                // FileOutputStream( "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/log=_10sec.csv",true ).writeCsv(beacons)
            }

        })

        container.bleHelper.setupBeaconScanning()
    }

    companion object {
        const val TAG = "BeaconReference"
        const val  LOGGING_PERIOD = 10000L
    }

}
//TODO CHANGE RUNBLOCKING COROUTINE SCOPE
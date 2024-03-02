/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bletracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.bletracker.data.AppPermissionManager
import com.example.bletracker.data.PermissionManager
import com.example.bletracker.data.ble.BLEHelper
import com.example.bletracker.ui.BLETrackerApp
import com.example.bletracker.ui.screens.PermissionViewModel
import com.example.bletracker.ui.theme.MarsPhotosTheme
import kotlinx.coroutines.launch
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region

class MainActivity : ComponentActivity() {
    private lateinit var permissionManager :PermissionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val regionViewModel = BeaconManager.getInstanceForApplication(this).getRegionViewModel(
            Region("all-beacons", null, null, null)
        )
        permissionManager = AppPermissionManager(applicationContext)
        setContent {
            MarsPhotosTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BLETrackerApp(regionViewModel,  permissionManager)
                }
            }
        }
    }
        override fun onResume() {
            super.onResume()
            lifecycleScope.launch {
                permissionManager.checkPermissions()
            }
        }

}


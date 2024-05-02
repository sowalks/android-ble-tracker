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
 *
 * Modified from MarsPhotos Code Lab from MarsPhtosApplication.kt.
 */


package uk.ac.cam.smw98.bletracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.ac.cam.smw98.bletracker.data.utils.PermissionManager
import uk.ac.cam.smw98.bletracker.ui.viewmodel.PermissionViewModel
import org.altbeacon.beacon.RegionViewModel




@Composable
fun BLETrackerApp(regionViewModel: RegionViewModel,permissionManager: PermissionManager) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            val permissionsViewModel: PermissionViewModel =
                viewModel(factory = PermissionViewModel.Factory(permissions = permissionManager).factory)
            val permissionsGranted =
                permissionsViewModel.uiState.collectAsState().value.hasAllAccess
            if (!permissionsGranted) {
                PermissionScreen(viewModel = permissionsViewModel, onConfirm = {})
            } else {
                ScreenTabLayout(
                    regionViewModel = regionViewModel,
                    snackBarHostState = snackBarHostState,
                    modifier = Modifier.fillMaxSize()
                )

            }
        }
    }
}




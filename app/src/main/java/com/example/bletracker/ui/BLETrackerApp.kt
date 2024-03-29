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


package com.example.bletracker.ui

import android.content.Intent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bletracker.data.utils.PermissionGroup
import com.example.bletracker.data.utils.PermissionManager
import com.example.bletracker.data.utils.State
import com.example.bletracker.ui.screens.PermissionScreen
import com.example.bletracker.ui.screens.ScreenTabLayout
import com.example.bletracker.ui.viewmodel.PermissionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                viewModel(factory = PermissionViewModel.Factory(permissions = permissionManager).Factory)
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


@Composable
@Preview
fun PreviewAppSnack()
{
    BLETrackerApp(regionViewModel =RegionViewModel(),permissionManager=FakePermissionsManager())
}
class FakePermissionsManager : PermissionManager {
    override val permissionGroups: Collection<PermissionGroup>
        get() = listOf()

    private val _state = MutableStateFlow(
        State(
            //if each group has access
            hasAccessGroups = listOf()
        )
    )
    override val state = _state.asStateFlow()
    val hasAllPermissions: Boolean
        get() = _state.value.hasAllAccess

    override fun checkPermissions() {
        TODO("Not yet implemented")
    }
    override fun createSettingsIntent(): Intent {
        TODO("Not yet implemented")
    }

}




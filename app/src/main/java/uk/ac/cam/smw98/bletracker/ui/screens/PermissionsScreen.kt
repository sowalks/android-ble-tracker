/*
 * Copyright (C) 2022 The Android Open Source Project
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
 * Modified from Privacy Code Lab.
 */

package uk.ac.cam.smw98.bletracker.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import uk.ac.cam.smw98.bletracker.ui.viewmodel.PermissionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel,
    onConfirm: () -> Unit
) {

    val state = viewModel.uiState.collectAsState()
    val permissionGroups = remember {
        viewModel.permissionGroups
    }
    val hasRequestedPermissions= remember { mutableStateListOf<Boolean>()}
    hasRequestedPermissions.addAll(permissionGroups.map{false})
    val context = LocalContext.current
    val requestPermissions =
        rememberLauncherForActivityResult(RequestMultiplePermissions()) {
            viewModel.onPermissionsChange()
        }

    fun openSettings() {
        ContextCompat.startActivity(context, viewModel.createSettingsIntent(), null)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Permissions needed") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "You have to grant access to these permissions in order to use the app"
            )
            permissionGroups.forEachIndexed { index, perm ->
                if (hasRequestedPermissions[index] && !state.value.hasAccessGroups[index]) {
                    Button(
                        onClick = { openSettings() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Go to settings for ${perm.title}")
                    }
                } else if (state.value.hasAccessGroups[index]) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text(perm.title)
                    }
                } else {
                    Button(onClick = {
                        requestPermissions.launch(perm.group)
                        hasRequestedPermissions[index] = true
                    }
                    ) {
                        Text(perm.title)
                    }
                }
            }
            if (state.value.hasAllAccess) {
                Button(onClick = { onConfirm }) {
                    Text("Get started")
                }
            }
        }
    }
}




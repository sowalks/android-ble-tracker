package uk.ac.cam.smw98.bletracker.ui.viewmodel


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
 * Modified from Privacy Codelab.
 */

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import uk.ac.cam.smw98.bletracker.data.utils.PermissionManager

class PermissionViewModel(val permissions: PermissionManager) : ViewModel() {

    val uiState = permissions.state
    val permissionGroups = permissions.permissionGroups

    fun onPermissionsChange() {
        permissions.checkPermissions()
    }

    fun createSettingsIntent(): Intent {
        return permissions.createSettingsIntent()
    }

    class Factory(permissions: PermissionManager) {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PermissionViewModel(permissions = permissions)
            }
        }
    }
}
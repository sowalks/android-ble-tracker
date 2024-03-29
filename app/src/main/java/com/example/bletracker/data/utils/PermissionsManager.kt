package com.example.bletracker.data.utils
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
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
//TODO: BR

interface PermissionManager{
    val state: StateFlow<State>
    val permissionGroups : Collection<PermissionGroup>
    fun checkPermissions()
    fun createSettingsIntent(): Intent
}
data class PermissionGroup(
    val title: String,
    val group: Array<String>
)

data class State(
    val hasAccessGroups: List<Boolean>
) {
    val hasAllAccess: Boolean
        get() = hasAccessGroups.all{it}
}

class AppPermissionManager(private val context: Context) : PermissionManager {

    override val permissionGroups: Collection<PermissionGroup> = permissionGroupsNeeded()

    private val _state = MutableStateFlow(
        State(
            //if each group has access
            hasAccessGroups = permissionGroups.map{permissionGroup -> hasAccess(permissionGroup.group).all{it}}
        )
    )
    override val state = _state.asStateFlow()
    val hasAllPermissions: Boolean
        get() = _state.value.hasAllAccess

    private fun hasAccess(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasAccess(permissions: Array<String>): List<Boolean> {
        return permissions.map(::hasAccess)
    }

    private fun permissionGroupsNeeded(): Collection<PermissionGroup> {
        val permissions : MutableList<PermissionGroup> = mutableListOf()
        permissions.add(PermissionGroup(title="Location",group=arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // As of version S (12) we need FINE_LOCATION, BLUETOOTH_SCAN and BACKGROUND_LOCATION
            // Manifest.permission.BLUETOOTH_CONNECT is not absolutely required to do just scanning,
            // but it is required if you want to access some info from the scans like the device name
            // and the additional cost of requesting this access is minimal, so we just request it
            permissions.add(PermissionGroup("Bluetooth", arrayOf(Manifest.permission.BLUETOOTH_SCAN)))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // As of version T (13) we POST_NOTIFICATIONS permissions if using a foreground service
            permissions.add(PermissionGroup("Notifications",arrayOf(Manifest.permission.POST_NOTIFICATIONS)))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // As of version Q (10) we need FINE_LOCATION and BACKGROUND_LOCATION (AND COARSE)
            permissions.add(PermissionGroup("Background Location",arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)))
        }

        return permissions
    }

    override fun checkPermissions() {
        val access = permissionGroups.map{permissionGroup -> hasAccess(permissionGroup.group).all{it}}
        _state.value = State(
            hasAccessGroups=access
        )
    }

    override fun createSettingsIntent(): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", context.packageName, null)
        }

        return intent
    }
}

package com.smartclipboard.ai.presentation.settings

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.smartclipboard.ai.collection.permissions.ImageAccessMode
import com.smartclipboard.ai.collection.permissions.MediaPermissionPolicy
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SettingsPermissionReader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun readImagePermissionState(): SettingsPermissionState {
        val permissions = MediaPermissionPolicy.requiredImagePermissions(
            sdkInt = Build.VERSION.SDK_INT,
            mode = ImageAccessMode.FullLibrary
        )
        return SettingsPermissionState(
            isGranted = permissions.isEmpty() || permissions.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            },
            requiredPermissions = permissions
        )
    }
}

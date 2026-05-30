package com.smartclipboard.ai.presentation.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.smartclipboard.ai.collection.media.MediaSyncManager
import com.smartclipboard.ai.collection.permissions.ImageAccessMode
import com.smartclipboard.ai.collection.permissions.MediaPermissionPolicy
import com.smartclipboard.ai.presentation.navigation.SmartClipboardRoot
import com.smartclipboard.ai.ui.theme.SmartClipboardTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var mediaSyncManager: MediaSyncManager

    private val requestImagePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) {
            syncMediaStoreImages()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartClipboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartClipboardRoot()
                }
            }
        }
        syncMediaStoreImagesIfPossible()
    }

    private fun syncMediaStoreImagesIfPossible() {
        val permissions = requiredImagePermissions()
        when {
            permissions.isEmpty() -> syncMediaStoreImages()
            permissions.all { permission -> hasPermission(permission) } -> syncMediaStoreImages()
            else -> requestImagePermissions.launch(permissions.toTypedArray())
        }
    }

    private fun syncMediaStoreImages() {
        lifecycleScope.launch {
            mediaSyncManager.syncNewImages()
        }
    }

    private fun requiredImagePermissions(): List<String> {
        return MediaPermissionPolicy.requiredImagePermissions(
            sdkInt = Build.VERSION.SDK_INT,
            mode = ImageAccessMode.FullLibrary
        )
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}

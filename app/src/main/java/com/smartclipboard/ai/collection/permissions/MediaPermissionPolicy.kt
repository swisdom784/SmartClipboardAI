package com.smartclipboard.ai.collection.permissions

import android.Manifest

enum class ImageAccessMode {
    FullLibrary,
    UserSelectedFallback
}

object MediaPermissionPolicy {
    fun requiredImagePermissions(
        sdkInt: Int,
        mode: ImageAccessMode
    ): List<String> {
        return when {
            sdkInt >= 34 && mode == ImageAccessMode.UserSelectedFallback -> {
                listOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            }

            sdkInt >= 33 -> {
                listOf(Manifest.permission.READ_MEDIA_IMAGES)
            }

            sdkInt >= 23 -> {
                listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            else -> emptyList()
        }
    }
}

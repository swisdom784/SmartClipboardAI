package com.smartclipboard.ai.collection.permissions

import android.Manifest
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaPermissionPolicyTest {
    @Test
    fun fullLibraryModeUsesReadMediaImagesOnAndroid13AndLater() {
        assertEquals(
            listOf(Manifest.permission.READ_MEDIA_IMAGES),
            MediaPermissionPolicy.requiredImagePermissions(
                sdkInt = 33,
                mode = ImageAccessMode.FullLibrary
            )
        )
        assertEquals(
            listOf(Manifest.permission.READ_MEDIA_IMAGES),
            MediaPermissionPolicy.requiredImagePermissions(
                sdkInt = 35,
                mode = ImageAccessMode.FullLibrary
            )
        )
    }

    @Test
    fun selectedPhotosFallbackUsesVisualUserSelectedOnAndroid14AndLater() {
        assertEquals(
            listOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED),
            MediaPermissionPolicy.requiredImagePermissions(
                sdkInt = 34,
                mode = ImageAccessMode.UserSelectedFallback
            )
        )
    }

    @Test
    fun oldAndroidVersionsUseReadExternalStorage() {
        assertEquals(
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            MediaPermissionPolicy.requiredImagePermissions(
                sdkInt = 32,
                mode = ImageAccessMode.FullLibrary
            )
        )
    }
}

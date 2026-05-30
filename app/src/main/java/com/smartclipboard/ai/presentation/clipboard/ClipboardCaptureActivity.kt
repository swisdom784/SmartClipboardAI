package com.smartclipboard.ai.presentation.clipboard

import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.smartclipboard.ai.R
import com.smartclipboard.ai.collection.clipboard.ClipboardCaptureHandler
import com.smartclipboard.ai.collection.clipboard.ClipboardCaptureResult
import com.smartclipboard.ai.collection.clipboard.ClipboardFailureReason
import com.smartclipboard.ai.collection.clipboard.ClipboardReader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClipboardCaptureActivity : ComponentActivity() {
    @Inject
    lateinit var clipboardCaptureHandler: ClipboardCaptureHandler

    private var hasCaptured = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !hasCaptured) {
            hasCaptured = true
            capturePrimaryClipOnce()
        }
    }

    private fun capturePrimaryClipOnce() {
        val clipboardManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(ClipboardManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
        }

        val payload = ClipboardReader.readPrimaryText(
            context = this,
            clipData = clipboardManager?.primaryClip
        )

        lifecycleScope.launch {
            val result = clipboardCaptureHandler.save(payload)
            Toast.makeText(
                this@ClipboardCaptureActivity,
                result.toMessageResId(),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun ClipboardCaptureResult.toMessageResId(): Int {
        return when (this) {
            is ClipboardCaptureResult.Success -> R.string.clipboard_saved_message
            is ClipboardCaptureResult.Failure -> when (reason) {
                ClipboardFailureReason.EmptyClipboard,
                ClipboardFailureReason.UnsupportedContent -> R.string.clipboard_empty_message
                ClipboardFailureReason.SaveFailed -> R.string.clipboard_save_failed_message
            }
        }
    }
}

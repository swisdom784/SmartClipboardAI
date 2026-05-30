package com.smartclipboard.ai.presentation.share

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.smartclipboard.ai.R
import com.smartclipboard.ai.collection.share.ShareContentHandler
import com.smartclipboard.ai.collection.share.ShareIntentReader
import com.smartclipboard.ai.collection.share.ShareSaveResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShareReceiverActivity : ComponentActivity() {
    @Inject
    lateinit var shareContentHandler: ShareContentHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val payload = ShareIntentReader.fromIntent(intent, contentResolver)
            val result = shareContentHandler.save(payload)
            Toast.makeText(
                this@ShareReceiverActivity,
                result.toMessageResId(),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun ShareSaveResult.toMessageResId(): Int {
        return when (this) {
            is ShareSaveResult.Success -> R.string.share_saved_message
            is ShareSaveResult.PartialSuccess -> R.string.share_partially_saved_message
            is ShareSaveResult.Failure -> R.string.share_save_failed_message
        }
    }
}

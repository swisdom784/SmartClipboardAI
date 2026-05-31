package com.smartclipboard.ai.presentation.share

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.smartclipboard.ai.collection.share.ShareContentHandler
import com.smartclipboard.ai.collection.share.ShareIntentReader
import com.smartclipboard.ai.presentation.feedback.SaveFeedbackMessageMapper
import com.smartclipboard.ai.presentation.feedback.SaveFeedbackToast
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
            SaveFeedbackToast.show(
                context = this@ShareReceiverActivity,
                message = SaveFeedbackMessageMapper.fromShareResult(result)
            )
            finish()
        }
    }
}

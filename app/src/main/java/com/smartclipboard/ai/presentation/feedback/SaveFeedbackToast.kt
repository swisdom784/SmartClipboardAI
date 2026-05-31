package com.smartclipboard.ai.presentation.feedback

import android.content.Context
import android.widget.Toast

object SaveFeedbackToast {
    fun show(
        context: Context,
        message: SaveFeedbackMessage
    ) {
        Toast.makeText(
            context,
            message.text,
            Toast.LENGTH_SHORT
        ).show()
    }
}

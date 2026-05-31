package com.smartclipboard.ai.processing.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MlKitOcrProcessor @Inject constructor(
    @ApplicationContext private val context: Context
) : OcrProcessor {
    private val recognizer: TextRecognizer =
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    override suspend fun recognizeText(uri: String): String {
        return withContext(Dispatchers.IO) {
            val image = InputImage.fromFilePath(context, Uri.parse(uri))
            recognizer.processSuspend(image).text.trim()
        }
    }

    private suspend fun TextRecognizer.processSuspend(image: InputImage): Text {
        return suspendCancellableCoroutine { continuation ->
            process(image)
                .addOnSuccessListener { result ->
                    if (continuation.isActive) {
                        continuation.resume(result)
                    }
                }
                .addOnFailureListener { throwable ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(throwable)
                    }
                }
        }
    }
}

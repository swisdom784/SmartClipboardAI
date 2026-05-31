package com.smartclipboard.ai.processing.ocr

interface OcrProcessor {
    suspend fun recognizeText(uri: String): String
}

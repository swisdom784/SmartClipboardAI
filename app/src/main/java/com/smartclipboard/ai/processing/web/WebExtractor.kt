package com.smartclipboard.ai.processing.web

interface WebExtractor {
    suspend fun extract(url: String): OpenGraphMetadata
}

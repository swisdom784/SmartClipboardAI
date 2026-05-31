package com.smartclipboard.ai.processing.web

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsoupWebExtractor @Inject constructor() : WebExtractor {
    override suspend fun extract(url: String): OpenGraphMetadata {
        return withContext(Dispatchers.IO) {
            val document = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MILLIS)
                .followRedirects(true)
                .get()

            OpenGraphMetadataParser.parseDocument(document)
        }
    }

    private companion object {
        const val TIMEOUT_MILLIS = 1_500
        const val USER_AGENT = "SmartClipboardAI/1.0"
    }
}

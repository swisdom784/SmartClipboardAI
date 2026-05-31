package com.smartclipboard.ai.processing.web

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object OpenGraphMetadataParser {
    fun parse(
        html: String,
        baseUrl: String? = null
    ): OpenGraphMetadata {
        return parseDocument(Jsoup.parse(html, baseUrl.orEmpty()))
    }

    fun parseDocument(document: Document): OpenGraphMetadata {
        return OpenGraphMetadata(
            title = document.firstMetaContent(
                "meta[property=og:title]",
                "meta[name=twitter:title]"
            ) ?: document.title().ifBlank { null },
            description = document.firstMetaContent(
                "meta[property=og:description]",
                "meta[name=twitter:description]",
                "meta[name=description]"
            ),
            imageUrl = document.firstAbsoluteMetaContent(
                "meta[property=og:image]",
                "meta[name=twitter:image]"
            )
        )
    }

    private fun Document.firstMetaContent(vararg selectors: String): String? {
        return selectors.firstNotNullOfOrNull { selector ->
            selectFirst(selector)
                ?.attr("content")
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
        }
    }

    private fun Document.firstAbsoluteMetaContent(vararg selectors: String): String? {
        return selectors.firstNotNullOfOrNull { selector ->
            val element = selectFirst(selector) ?: return@firstNotNullOfOrNull null
            val absoluteUrl = element.absUrl("content").trim()
            absoluteUrl.ifBlank {
                element.attr("content").trim()
            }.takeIf { it.isNotEmpty() }
        }
    }
}

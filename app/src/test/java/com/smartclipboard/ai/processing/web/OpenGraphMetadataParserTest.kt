package com.smartclipboard.ai.processing.web

import org.junit.Assert.assertEquals
import org.junit.Test

class OpenGraphMetadataParserTest {
    @Test
    fun `parses open graph metadata from html`() {
        val html = """
            <html>
                <head>
                    <meta property="og:title" content="공식 일정 안내">
                    <meta property="og:description" content="행사 일정과 준비물">
                    <meta property="og:image" content="/images/cover.png">
                </head>
            </html>
        """.trimIndent()

        val metadata = OpenGraphMetadataParser.parse(
            html = html,
            baseUrl = "https://example.com/event"
        )

        assertEquals("공식 일정 안내", metadata.title)
        assertEquals("행사 일정과 준비물", metadata.description)
        assertEquals("https://example.com/images/cover.png", metadata.imageUrl)
    }

    @Test
    fun `falls back to html title and description meta`() {
        val html = """
            <html>
                <head>
                    <title>예약 확인</title>
                    <meta name="description" content="예약 번호와 방문 시간">
                </head>
            </html>
        """.trimIndent()

        val metadata = OpenGraphMetadataParser.parse(html = html)

        assertEquals("예약 확인", metadata.title)
        assertEquals("예약 번호와 방문 시간", metadata.description)
        assertEquals(null, metadata.imageUrl)
    }
}

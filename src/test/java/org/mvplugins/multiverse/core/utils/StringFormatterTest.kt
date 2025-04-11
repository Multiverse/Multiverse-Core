package org.mvplugins.multiverse.core.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class StringFormatterTest {
    @Test
    fun `StringFormatter parseQuotesInArgs`() {
        assertEquals(
            listOf("before", "this is a test", "after"),
            StringFormatter.parseQuotesInArgs(arrayOf("before", "\"this", "is", "a", "test\"", "after"))
        )
        assertEquals(
            listOf("\""),
            StringFormatter.parseQuotesInArgs(arrayOf("\""))
        )
        assertEquals(
            listOf("\"", "after"),
            StringFormatter.parseQuotesInArgs(arrayOf("\"", "after"))
        )
        assertEquals(
            listOf("\"word", "after"),
            StringFormatter.parseQuotesInArgs(arrayOf("\"word", "after"))
        )
        assertEquals(
            listOf("word\"", "after\""),
            StringFormatter.parseQuotesInArgs(arrayOf("word\"", "after\""))
        )
        assertEquals(
            listOf("word\"", "af\"ter"),
            StringFormatter.parseQuotesInArgs(arrayOf("word\"", "af\"ter"))
        )
    }

    @Test
    fun `StringFormatter quoteMultiWordString`() {
        assertEquals(
            "\"this is a test\"",
            StringFormatter.quoteMultiWordString("this is a test")
        )
        assertEquals(
            "test",
            StringFormatter.quoteMultiWordString("test")
        )
    }
}

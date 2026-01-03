package org.mvplugins.multiverse.core.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
        assertNull(StringFormatter.quoteMultiWordString(null))
    }

    @Test
    fun `StringFormatter parseCSVMap`() {
        assertEquals(
            emptyMap<String, String>(),
            StringFormatter.parseCSVMap("")
        )
        assertEquals(
            mapOf("key" to "value"),
            StringFormatter.parseCSVMap("key=value")
        )
        assertEquals(
            mapOf("key1" to "value1", "key2" to "value2", "key3" to "value3"),
            StringFormatter.parseCSVMap("key1=value1,key2=value2,key3=value3")
        )
    }
}

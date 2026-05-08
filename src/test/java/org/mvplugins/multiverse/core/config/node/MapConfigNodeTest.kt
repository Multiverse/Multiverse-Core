package org.mvplugins.multiverse.core.config.node

import io.vavr.control.Try
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapConfigNodeTest {

    @Test
    fun `MapConfigNode parses from string with defaults`() {
        val node: MapConfigNode<String, Int> =
            MapConfigNode.mapBuilder("my-map", String::class.java, Int::class.javaObjectType).build()
        val expected: Map<String, Int> = mapOf("key1" to 1, "key2" to 2)

        assertEquals(
            expected,
            node.parseFromString("key1=1;key2=2").get()
        )
        assertEquals(emptyMap(), node.parseFromString("  ").get())
    }

    @Test
    fun `MapConfigNode default serializer converts values by key and value type`() {
        val node: MapConfigNode<String, Int> =
            MapConfigNode.mapBuilder("my-map", String::class.java, Int::class.javaObjectType).build()
        val serializer = node.serializer
        val expected: Map<String, Int> = mapOf("alpha" to 4, "beta" to 8)
        val rawMap: Map<String, Any> = mapOf("alpha" to "4", "beta" to 8)

        val deserialized = serializer!!.deserialize(rawMap, node.type)
        assertEquals(expected, deserialized)

        val serialized = serializer.serialize(expected, node.type)
        assertEquals(expected, serialized)
    }

    @Test
    fun `MapConfigNode key and value validators are used for node validation`() {
        val node: MapConfigNode<String, Int> =
            MapConfigNode.mapBuilder("my-map", String::class.java, Int::class.javaObjectType)
            .keyValidator { key ->
                if (key.startsWith("k")) Try.success(null) else Try.failure(IllegalArgumentException("invalid key"))
            }
            .valueValidator { value ->
                if (value >= 0) Try.success(null) else Try.failure(IllegalArgumentException("invalid value"))
            }
            .build()

        assertTrue(node.validate(mapOf("key" to 1)).isSuccess)
        assertTrue(node.validate(mapOf("bad" to 1)).isFailure)
        assertTrue(node.validate(mapOf("key" to -1)).isFailure)
    }
}

package org.mvplugins.multiverse.core.utils

import org.mvplugins.multiverse.core.utils.matcher.ExactStringMatcher
import org.mvplugins.multiverse.core.utils.matcher.RegexStringMatcher
import org.mvplugins.multiverse.core.utils.matcher.WildcardStringMatcher
import kotlin.test.Test

class StringMatcherTest {

    @Test
    fun `exact string matcher - single string`() {
        val matcher = ExactStringMatcher("test")
        assert(matcher.matches("test"))
        assert(!matcher.matches("Test"))
        assert(!matcher.matches("testing"))
    }

    @Test
    fun `exact string matcher - multiple strings`() {
        val matcher = ExactStringMatcher(setOf("test", "example", "sample"));
        assert(matcher.matches("test"))
        assert(matcher.matches("example"))
        assert(matcher.matches("sample"))
        assert(!matcher.matches("Test"))
        assert(!matcher.matches("testing"))
    }

    @Test
    fun `wildcard string matcher - single`() {
        val matcher = WildcardStringMatcher("test*")
        assert(matcher.matches("test"))
        assert(matcher.matches("test123"))
        assert(matcher.matches("testing"))
        assert(!matcher.matches("TEST"))
        assert(!matcher.matches("nottest123"))
    }

    @Test
    fun `wildcard string matcher - multiple`() {
        val matcher = WildcardStringMatcher("test-*_*-world")
        assert(matcher.matches("test-123_pl-world"))
        assert(matcher.matches("test-abc_xyz-world"))
        assert(matcher.matches("test-hello_world-world"))
        assert(!matcher.matches("test-hello-world"))
        assert(!matcher.matches("test-hello_world-world!"))
        assert(!matcher.matches("TEST-123_PL-WORLD"))
    }

    @Test
    fun `regex string matcher`() {
        val matcher = RegexStringMatcher("r=^test[0-9]+$")
        assert(matcher.matches("test123"))
        assert(matcher.matches("test456"))
        assert(!matcher.matches("test"))
        assert(!matcher.matches("TEST123"))
        assert(!matcher.matches("testing"))
    }
}

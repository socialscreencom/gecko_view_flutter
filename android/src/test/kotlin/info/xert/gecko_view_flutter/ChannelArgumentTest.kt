package info.xert.gecko_view_flutter

import info.xert.gecko_view_flutter.common.NoArgumentException
import info.xert.gecko_view_flutter.common.tryExtractOptionalSingleArgument
import info.xert.gecko_view_flutter.common.tryExtractSingleArgument
import io.flutter.plugin.common.MethodCall
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

internal class ChannelArgumentTest {

    @Test
    fun extractsAPresentArgument() {
        val call = MethodCall("openURI", mapOf("tabId" to 7))

        assertEquals(7, tryExtractSingleArgument<Int>(call, "tabId"))
    }

    @Test
    fun rejectsAMissingArgument() {
        val call = MethodCall("openURI", mapOf("uri" to "https://example.com"))

        assertFailsWith<NoArgumentException> {
            tryExtractSingleArgument<Int>(call, "tabId")
        }
    }

    @Test
    fun rejectsAMissingArgumentEvenWhenOptional() {
        val call = MethodCall("openURI", mapOf<String, Any>())

        assertFailsWith<NoArgumentException> {
            tryExtractOptionalSingleArgument<String>(call, "uri")
        }
    }

    @Test
    fun passesThroughAnExplicitNull() {
        val call = MethodCall("openURI", mapOf("uri" to null))

        assertNull(tryExtractOptionalSingleArgument<String>(call, "uri"))
    }
}

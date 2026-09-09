package info.xert.gecko_view_flutter

import info.xert.gecko_view_flutter.common.GeckoViewException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class GeckoViewExceptionTest {

    /*
     * Flutter's MethodChannel.IncomingMethodCallHandler catches RuntimeException
     * and replies with an error envelope. Anything deriving from Error is not
     * caught there: it unwinds to the main looper and kills the host app. Every
     * failed lookup in this plugin used to throw java.lang.InternalError, so a
     * stale tab id was fatal to the app embedding it.
     *
     * If this test fails, that whole class of crash is back.
     */
    @Test
    fun isReportableRatherThanFatal() {
        val thrown: Throwable = GeckoViewException("Tab does not exist")

        assertTrue(thrown is RuntimeException, "must be caught by MethodChannel")
        assertFalse(thrown is Error, "an Error would kill the host app")
    }

    @Test
    fun carriesItsMessageAndCause() {
        val cause = IllegalStateException("session closed")
        val thrown = GeckoViewException("Tab does not exist", cause)

        assertEquals("Tab does not exist", thrown.message)
        assertEquals(cause, thrown.cause)
    }
}

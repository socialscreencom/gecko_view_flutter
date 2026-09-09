package info.xert.gecko_view_flutter.common

/**
 * Signals a call the plugin cannot serve: an unknown tab, a session in the
 * wrong state, or an extension that is not installed.
 *
 * This extends RuntimeException rather than Error deliberately. Flutter's
 * MethodChannel.IncomingMethodCallHandler catches RuntimeException and replies
 * with an error envelope, which reaches Dart as a catchable PlatformException.
 * An Error is not caught there: it unwinds to the main looper and kills the
 * process, so a stale tab id took the whole app down.
 */
class GeckoViewException(message: String, cause: Throwable? = null)
    : RuntimeException(message, cause)

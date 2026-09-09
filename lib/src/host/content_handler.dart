/// Reports that the content process behind a tab went away.
///
/// GeckoView rebuilds the tab in place, so without this notification a
/// recovered crash is indistinguishable from a player that never faltered.
class ContentCrash {
  /// Tab whose content process died.
  final int tabId;

  /// "crash" when the process crashed, "kill" when the system reclaimed it.
  final String reason;

  /// Whether the tab was rebuilt and its URL reopened.
  final bool recovered;

  /// URL the tab was showing, if it could still be read.
  final String? url;

  ContentCrash(this.tabId, this.reason, this.recovered, this.url);

  ContentCrash.fromMap(Map<Object?, Object?> map)
      : tabId = map["tabId"] as int,
        reason = map["reason"] as String,
        recovered = map["recovered"] as bool,
        url = map["url"] as String?;
}

typedef ContentCrashHandler = Future<void> Function(ContentCrash);

class ContentHandler {
  /// Defaults to doing nothing: a notification no one listens for must not
  /// fail, unlike a prompt that has to produce an answer.
  ContentCrashHandler onContentCrash = (_) async {};
}

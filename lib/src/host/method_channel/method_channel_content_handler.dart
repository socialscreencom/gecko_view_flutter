import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:gecko_view_flutter/src/host/content_handler.dart';

class MethodChannelContentHandler extends ContentHandler {
  final MethodChannel _channel;

  MethodChannelContentHandler(
      this._channel,
  ) : super() {
    _channel.setMethodCallHandler((call) async {
      switch (call.method) {
        case "contentCrash":
          try {
            await onContentCrash(
                ContentCrash.fromMap(call.arguments as Map<Object?, Object?>));
          }
          catch(e) {
            debugPrint(e.toString());
          }
      }
    });
  }
}

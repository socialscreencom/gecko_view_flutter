import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gecko_view_flutter/gecko_view_flutter.dart';
import 'package:gecko_view_flutter/src/host/method_channel/method_channel_content_handler.dart';
import 'package:gecko_view_flutter/src/host/method_channel/method_channel_proxy.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group("ContentCrash.fromMap", () {
    test("reads a recovered crash", () {
      final crash = ContentCrash.fromMap(const <Object?, Object?>{
        "tabId": 3,
        "reason": "crash",
        "recovered": true,
        "url": "https://example.com/a",
      });

      expect(crash.tabId, 3);
      expect(crash.reason, "crash");
      expect(crash.recovered, isTrue);
      expect(crash.url, "https://example.com/a");
    });

    test("tolerates an unknown url, which is what a failed recovery reports",
        () {
      final crash = ContentCrash.fromMap(const <Object?, Object?>{
        "tabId": 0,
        "reason": "kill",
        "recovered": false,
        "url": null,
      });

      expect(crash.recovered, isFalse);
      expect(crash.url, isNull);
    });
  });

  test("a contentCrash call on the view channel reaches the handler", () async {
    const channel = MethodChannel("gecko_view_flutter_11");
    final handler = MethodChannelContentHandler(channel);

    ContentCrash? received;
    handler.onContentCrash = (crash) async {
      received = crash;
    };

    // Simulate the platform invoking the method on this channel.
    await TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .handlePlatformMessage(
      channel.name,
      const StandardMethodCodec().encodeMethodCall(
        const MethodCall("contentCrash", <Object?, Object?>{
          "tabId": 1,
          "reason": "kill",
          "recovered": true,
          "url": "https://example.com/b",
        }),
      ),
      (_) {},
    );

    expect(received, isNotNull);
    expect(received!.tabId, 1);
    expect(received!.reason, "kill");
    expect(received!.recovered, isTrue);
  });

  test("a platform error on a tab call surfaces as a PlatformException",
      () async {
    // The whole point of raising RuntimeException on the Android side: an
    // unknown tab has to reach Dart as something catchable rather than
    // killing the process.
    const channel = MethodChannel("gecko_view_flutter_12_tab");
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (call) async {
      throw PlatformException(
        code: "Gecko view error",
        message: "Tab does not exist",
      );
    });

    await expectLater(
      MethodChannelProxy.invokeMethodForTab<void>(12, 0, "openURI", {
        "uri": "https://example.com",
      }),
      throwsA(
        isA<PlatformException>()
            .having((e) => e.code, "code", "Gecko view error")
            .having((e) => e.message, "message", "Tab does not exist"),
      ),
    );
  });
}

# gecko_view_flutter

GeckoView support for Flutter.
Android support only.


## Testing

Two suites, both run by `.github/workflows/ci.yml`.

Dart:

```bash
flutter pub get
flutter analyze --no-fatal-infos
flutter test
```

Kotlin:

```bash
cd example && flutter build apk --debug
cd android && ./gradlew :gecko_view_flutter:testDebugUnitTest
```

`gradlew` is gitignored; Flutter injects it during a build, so the example has
to be built once before the Gradle command works.

The example pins the same Gradle, Android Gradle plugin and Kotlin versions as
flutter-player, the consumer. Keep them in step: a toolchain the consumer does
not use is a toolchain this repository cannot vouch for.

## Errors

Failures cross the method channel as `PlatformException`, so callers can catch
and report them. Anything raised inside the plugin must therefore derive from
`RuntimeException` — `GeckoViewException` does. A `java.lang.Error` is not
caught by Flutter's `MethodChannel` and kills the host app instead.

`GeckoViewController.onContentCrash` reports a tab whose content process died,
and whether the tab was rebuilt.

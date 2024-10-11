package info.xert.gecko_view_flutter

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

/** GeckoViewFlutterPlugin */
class GeckoViewFlutterPlugin: FlutterPlugin {

    private lateinit var runtimeController: GeckoRuntimeController
    private var proxy: GeckoProxy? = null

    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        runtimeController = GeckoRuntimeController(binding.applicationContext, binding.flutterAssets)
        proxy = GeckoProxy(binding.binaryMessenger, runtimeController)

        binding.platformViewRegistry.registerViewFactory(
            "gecko_view", GeckoViewFactory(binding.binaryMessenger, runtimeController)
        )
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        proxy?.dispose()
        proxy = null
    }
}

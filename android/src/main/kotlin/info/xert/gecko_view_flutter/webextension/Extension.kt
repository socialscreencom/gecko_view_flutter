package info.xert.gecko_view_flutter.webextension

import android.os.Handler
import android.os.Looper
import android.util.Log
import info.xert.gecko_view_flutter.common.ResultConsumer
import info.xert.gecko_view_flutter.common.GeckoViewException
import io.flutter.embedding.engine.plugins.FlutterPlugin
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.WebExtension
import org.mozilla.geckoview.WebExtension.PortDelegate

abstract class Extension {
    abstract val TAG: String

    abstract val extensionID: String
    abstract val extensionPath: String

    var enabled: Boolean = false
        private set

    var messageHandler: (Any) -> Unit = {
        _: Any -> Unit
    }

    protected val portDelegate: WebExtension.PortDelegate = object: PortDelegate {
        override fun onPortMessage(message: Any, port: WebExtension.Port) {
            messageHandler(message)
        }
    }

    protected val messageDelegate: WebExtension.MessageDelegate = object: WebExtension.MessageDelegate {
        override fun onConnect(newPort: WebExtension.Port) {
            newPort.setDelegate(portDelegate)
            port = newPort
        }
    }

    protected var port: WebExtension.Port? = null;
    var extension: WebExtension? = null
        private set
    fun enable(runtime: GeckoRuntime, assets: FlutterPlugin.FlutterAssets, callback: ResultConsumer<Unit>) {
        Log.d(TAG, "Initializing $extensionID Extension")

        if (extension != null) {
            // Already installed. Answering matters: every path out of this
            // method has to reach the callback, or the caller's future never
            // completes and enabling an extension twice hangs for good.
            callback.success(Unit)
            return
        }

        val extensionPath = assets.getAssetFilePathBySubpath(extensionPath, "gecko_view_flutter")
                ?: throw GeckoViewException("Invalid plugin installation")

        runtime.webExtensionController.ensureBuiltIn("resource://android/assets/$extensionPath", extensionID)
                .accept(
                        { newExtension ->
                            Handler(Looper.getMainLooper()).post {
                                if (newExtension == null) {
                                    // Reporting success would leave the caller
                                    // believing an extension that was never
                                    // installed is ready to use.
                                    callback.error(
                                            TAG,
                                            "$extensionID Extension was not installed",
                                            null
                                    )
                                    return@post
                                }

                                extension = newExtension
                                newExtension.setMessageDelegate(
                                        messageDelegate,
                                        "browser"
                                )

                                enabled = true
                                Log.d(TAG, "$extensionID Extension initialized")

                                callback.success(Unit)
                            }
                        },
                        { e ->
                            Log.e(TAG, "Error registering $extensionID Extension", e)
                            // Same looper as the success path: a
                            // MethodChannel.Result has to be answered on the
                            // main thread. The message rather than the
                            // Throwable, because the codec cannot encode one.
                            Handler(Looper.getMainLooper()).post {
                                callback.error(
                                        TAG,
                                        "Error registering $extensionID Extension",
                                        e?.message
                                )
                            }
                        }
                )
    }
}
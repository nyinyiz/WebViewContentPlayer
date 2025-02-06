package com.nyinyi.assignment.webviewcontentplayer

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import kotlin.apply
import kotlin.text.replace
import kotlin.text.trimIndent

class SC_INTERFACE(private val context: Context) {

    // JavaScript interface functions
    @JavascriptInterface
    fun device_info(): String {
        return try {
            val nativeData = getNativeInfo()
            JSONObject().apply {
                put("app_version", "1.0")
                put("package_name", context.packageName)
                put("screen_width", Resources.getSystem().displayMetrics.widthPixels)
                put("screen_height", Resources.getSystem().displayMetrics.heightPixels)
                put("screen_density", Resources.getSystem().displayMetrics.density)
                put("android_version", Build.VERSION.SDK_INT)
                put("device_manufacturer", Build.MANUFACTURER)
                put("device_model", Build.MODEL)
                put("native_info", JSONObject(nativeData))
            }.toString(4)
        } catch (e: Exception) {
            JSONObject().apply {
                put("error", "Failed to get device info: ${e.message}")
            }.toString(4)
        }
    }

    @JavascriptInterface
    fun take_screenshot(callback: String) {
        try {
            val webView = getWebViewInstance()
            webView.post {
                try {
                    // Wait for any animations to complete
                    Thread.sleep(100)
                    
                    // Create bitmap of the entire WebView
                    val bitmap = Bitmap.createBitmap(
                        webView.width,
                        webView.height,
                        Bitmap.Config.ARGB_8888
                    )
                    
                    // Draw the WebView content to the bitmap
                    val canvas = Canvas(bitmap)
                    webView.draw(canvas)
                    
                    // Convert to base64 in background thread
                    Thread {
                        try {
                            val outputStream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            val base64String = Base64.encodeToString(
                                outputStream.toByteArray(),
                                Base64.DEFAULT
                            )
                            
                            // Escape special characters in the base64 string
                            val escapedBase64 = base64String.replace("\n", "")
                            
                            // Call the JavaScript callback on UI thread
                            webView.post {
                                webView.evaluateJavascript("""
                                    (function() {
                                        try {
                                            if (typeof handleScreenshot === 'function') {
                                                handleScreenshot('data:image/png;base64,$escapedBase64');
                                            } else {
                                                console.error('handleScreenshot function not found');
                                            }
                                        } catch(e) {
                                            console.error('Screenshot callback failed:', e);
                                        }
                                    })();
                                """.trimIndent(), null)
                            }
                            
                            // Cleanup
                            outputStream.close()
                            bitmap.recycle()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            webView.post {
                                webView.evaluateJavascript(
                                    "console.error('Screenshot processing failed: ${e.message}')",
                                    null
                                )
                            }
                        }
                    }.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                    webView.evaluateJavascript(
                        "console.error('Screenshot capture failed: ${e.message}')",
                        null
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JavascriptInterface
    fun get_name(): String {
        return try {
            val name = "Nyi Nyi Zaw" // Your name
            println("get_name called, returning: $name") // Debug log
            name
        } catch (e: Exception) {
            println("Error in get_name: ${e.message}") // Debug log
            "Error: ${e.message}"
        }
    }

    // Native function (JNI)
    external fun getNativeInfo(): String

    companion object {
        init {
            try {
                System.loadLibrary("native-lib")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Store WebView instance for screenshot functionality
        private var webViewInstance: WebView? = null

        fun setWebViewInstance(webView: WebView) {
            webViewInstance = webView
        }

        fun getWebViewInstance(): WebView {
            return webViewInstance ?: throw IllegalStateException("WebView instance not set")
        }
    }
}
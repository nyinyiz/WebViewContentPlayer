package com.nyinyi.assignment.webviewcontentplayer.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.nyinyi.assignment.webviewcontentplayer.SC_INTERFACE
import org.json.JSONObject

@SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
@Composable
fun HomeScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val webView = rememberWebView()

    // Initialize SC_INTERFACE and attach to WebView
    val scInterface = remember { SC_INTERFACE(context) }
    LaunchedEffect(webView) {
        // Set up WebView instance for screenshot functionality
        SC_INTERFACE.setWebViewInstance(webView)

        // Clear any existing data
        webView.clearCache(true)
        webView.clearHistory()

        webView.loadUrl("file:///android_asset/slideshow.html")
        // Add JavaScript interfaces
        webView.addJavascriptInterface(scInterface, "android")
        webView.addJavascriptInterface(scInterface, "SC_INTERFACE")

        // Add debug button (optional)
        webView.evaluateJavascript(
            """
            console.log('WebView dimensions:', {
                width: window.innerWidth,
                height: window.innerHeight
            });
        """.trimIndent(), null
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // WebView
        AndroidView(
            factory = { webView },
            update = { it.loadUrl("file:///android_asset/slideshow.html") },
            modifier = Modifier
                .fillMaxSize()
                .testTag("webview")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                val deviceInfoJson = scInterface.device_info()
                showDeviceInfoDialog(context, deviceInfoJson)
            }) {
                Text("Device Info")
            }
            Button(onClick = {
                webView.evaluateJavascript(
                    "window.SC_INTERFACE.take_screenshot('handleScreenshot')",
                    null
                )
            }) {
                Text("Screenshot")
            }
        }
    }
}

@Composable
fun rememberWebView(): WebView {
    val context = LocalContext.current
    return remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                // JavaScript settings
                javaScriptEnabled = true
                domStorageEnabled = true

                // Media settings
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = true
                allowContentAccess = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true

                // Performance settings
                setRenderPriority(WebSettings.RenderPriority.HIGH)
                cacheMode = WebSettings.LOAD_DEFAULT

                // Layout settings
                useWideViewPort = true
                loadWithOverviewMode = true

                // Mixed content settings
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                // Hardware acceleration
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                // Debug settings
                WebView.setWebContentsDebuggingEnabled(true)

                // Additional settings for content loading
                loadsImagesAutomatically = true
                blockNetworkImage = false
                databaseEnabled = true

                // Additional JavaScript settings
                javaScriptCanOpenWindowsAutomatically = true

                // Layout settings
                layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

                // Additional media settings
                setSupportMultipleWindows(true)

                // Enable DOM storage
                domStorageEnabled = true

                // Set cache mode
                cacheMode = WebSettings.LOAD_NO_CACHE // or LOAD_DEFAULT for normal caching
            }

            // Set up WebChromeClient for console messages and permissions
            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest?) {
                    request?.grant(request.resources)
                }

                override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                    println("WebView Console: ${message.message()}")
                    return true
                }
            }
        }
    }
}

private fun showDeviceInfoDialog(context: Context, message: String) {
    AlertDialog.Builder(context)
        .setTitle("Device Info")
        .setMessage(formatJson(message))
        .setPositiveButton("OK", null)
        .show()
}

private fun formatJson(json: String): String {
    return try {
        val jsonObject = JSONObject(json)
        jsonObject.toString(4)
    } catch (e: Exception) {
        json
    }
}
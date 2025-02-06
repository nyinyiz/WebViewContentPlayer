package com.nyinyi.assignment.webviewcontentplayer.screens

import android.annotation.SuppressLint
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
@Composable
fun HomeScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val webView = rememberWebView()

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

            }) {
                Text("Device Info")
            }
            Button(onClick = {

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

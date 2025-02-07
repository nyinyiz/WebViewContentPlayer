package com.nyinyi.assignment.webviewcontentplayer.components

import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

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

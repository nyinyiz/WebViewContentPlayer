package com.nyinyi.assignment.webviewcontentplayer.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.nyinyi.assignment.webviewcontentplayer.SC_INTERFACE
import com.nyinyi.assignment.webviewcontentplayer.components.DeviceInfoDialog
import com.nyinyi.assignment.webviewcontentplayer.components.rememberWebView

@SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
@Composable
fun HomeScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val webView = rememberWebView()
    var showDeviceInfoDialog by remember { mutableStateOf(false) }
    var deviceInfo by remember { mutableStateOf("") }

    // Initialize SC_INTERFACE and attach to WebView
    val scInterface = remember { SC_INTERFACE(context) }

    // Set up WebView and JavaScript interface
    LaunchedEffect(webView) {
        setupWebView(webView, scInterface)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // WebView
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize()
                .testTag("webview")
        )

        // Buttons for Device Info and Screenshot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                val deviceInfoJson = scInterface.device_info()
                showDeviceInfoDialog = true
                deviceInfo = deviceInfoJson
            }) {
                Text("Device Info")
            }
            Spacer(modifier = Modifier.width(8.dp))
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

    // Show Device Info Dialog if needed
    if (showDeviceInfoDialog) {
        DeviceInfoDialog(
            message = deviceInfo,
            onDismiss = { showDeviceInfoDialog = false }
        )
    }
}

// Helper function to set up WebView
private fun setupWebView(webView: WebView, scInterface: SC_INTERFACE) {
    // Set up WebView instance for screenshot functionality
    SC_INTERFACE.setWebViewInstance(webView)

    with(webView) {
        clearCache(true)
        clearHistory()
        loadUrl("file:///android_asset/slideshow.html")

        // Add JavaScript interfaces
        addJavascriptInterface(scInterface, "android")
        addJavascriptInterface(scInterface, "SC_INTERFACE")

        // Debugging: Log WebView dimensions
        evaluateJavascript(
            """
            console.log('WebView dimensions:', {
                width: window.innerWidth,
                height: window.innerHeight
            });
        """.trimIndent(), null
        )
    }
}
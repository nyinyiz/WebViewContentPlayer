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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.nyinyi.assignment.webviewcontentplayer.SC_INTERFACE
import org.json.JSONObject
import androidx.compose.ui.graphics.Color

@SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
@Composable
fun HomeScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val webView = rememberWebView()

    var showDeviceInfoDialog by remember { mutableStateOf(false) }
    var deviceInfo by remember { mutableStateOf("") }

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
                showDeviceInfoDialog = true
                deviceInfo = deviceInfoJson
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

    if (showDeviceInfoDialog) {
        DeviceInfoDialog(
            message = deviceInfo,
            onDismiss = { showDeviceInfoDialog = false }
        )
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

@Composable
private fun DeviceInfoDialog(
    message: String,
    onDismiss: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("JSON Format", "User Friendly")
    
    val jsonText = remember(message) { formatJson(message) }
    val userFriendlyText = remember(message) { createUserFriendlyFormat(message) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Device Info") },
        text = {
            Column {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
                
                val scrollState = rememberScrollState()
                if (selectedTabIndex == 0) {
                    Text(
                        text = jsonText,
                        modifier = Modifier
                            .height(400.dp)
                            .verticalScroll(scrollState)
                            .padding(top = 16.dp),
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Text(
                        text = userFriendlyText,
                        modifier = Modifier
                            .height(400.dp)
                            .verticalScroll(scrollState)
                            .padding(top = 16.dp),
                        fontFamily = FontFamily.Default
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

private fun formatJson(json: String): String {
    return try {
        val jsonObject = JSONObject(json)
        jsonObject.toString(4)
    } catch (e: Exception) {
        json
    }
}

private fun createUserFriendlyFormat(jsonString: String): String {
    return buildAnnotatedString {
        try {
            val json = JSONObject(jsonString)
            
            // App Info
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))) {
                append("App Information\n")
            }
            append("• Version: ${json.optString("app_version")}\n")
            append("• Package: ${json.optString("package_name")}\n\n")
            
            // Screen Info
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))) {
                append("Screen Information\n")
            }
            append("• Width: ${json.optString("screen_width")} pixels\n")
            append("• Height: ${json.optString("screen_height")} pixels\n")
            append("• Density: ${json.optString("screen_density")}\n\n")
            
            // Device Info
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))) {
                append("Device Information\n")
            }
            append("• Manufacturer: ${json.optString("device_manufacturer")}\n")
            append("• Model: ${json.optString("device_model")}\n")
            append("• Android Version: ${json.optString("android_version")}\n\n")
            
            // Native Info
            val nativeInfo = json.optJSONObject("native_info")
            if (nativeInfo != null) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))) {
                    append("System Information\n")
                }
                append("• RAM Total: ${formatBytes(nativeInfo.optString("ram_total"))}\n")
                
                val cpuInfo = nativeInfo.optJSONObject("cpu_info")
                if (cpuInfo != null) {
                    append("• CPU Cores: ${cpuInfo.optString("cpu_cores")}\n")
                    append("• CPU Frequency: ${cpuInfo.optString("cpu_freq")} MHz\n")
                }
                
                append("• Kernel Version: ${nativeInfo.optString("kernel_version")}\n")
                append("• Build Fingerprint: ${nativeInfo.optString("build_fingerprint")}\n")
                append("• Hardware Serial: ${nativeInfo.optString("hardware_serial")}\n")
            }
        } catch (e: Exception) {
            append("Error parsing device information: ${e.message}")
        }
    }.toString()
}

private fun formatBytes(bytes: String): String {
    return try {
        val bytesLong = bytes.toLong()
        when {
            bytesLong >= 1024 * 1024 * 1024 -> String.format(
                "%.2f GB",
                bytesLong / (1024.0 * 1024.0 * 1024.0)
            )

            bytesLong >= 1024 * 1024 -> String.format("%.2f MB", bytesLong / (1024.0 * 1024.0))
            bytesLong >= 1024 -> String.format("%.2f KB", bytesLong / 1024.0)
            else -> "$bytesLong bytes"
        }
    } catch (e: Exception) {
        bytes
    }
}
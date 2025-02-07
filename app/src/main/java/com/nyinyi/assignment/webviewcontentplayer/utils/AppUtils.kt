package com.nyinyi.assignment.webviewcontentplayer.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import org.json.JSONObject

fun formatJson(json: String): String {
    return try {
        val jsonObject = JSONObject(json)
        jsonObject.toString(4)
    } catch (e: Exception) {
        json
    }
}

fun createUserFriendlyFormat(jsonString: String): String {
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
package com.nyinyi.assignment.webviewcontentplayer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.nyinyi.assignment.webviewcontentplayer.utils.createUserFriendlyFormat
import com.nyinyi.assignment.webviewcontentplayer.utils.formatJson

@Composable
fun DeviceInfoDialog(
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
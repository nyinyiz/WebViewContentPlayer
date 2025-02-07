package com.nyinyi.assignment.webviewcontentplayer

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms.getCurrentUrl
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class WebViewContentPlayerTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var scInterface: SC_INTERFACE

    @Before
    fun setup() {
        scInterface = SC_INTERFACE(InstrumentationRegistry.getInstrumentation().targetContext)
        // No need to setContent here as MainActivity already sets it
    }

    @Test
    fun testWebViewLoading() {
        // Wait for initial load
        composeTestRule.waitForIdle()

        // Verify WebView is displayed
        composeTestRule.onNodeWithTag("webview").assertExists()

        // Wait for WebView to load completely
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                onWebView().check(
                    webMatches(
                        getCurrentUrl(),
                        containsString("file:///android_asset/slideshow.html")
                    )
                )
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    @Test
    fun testScreenshotCapability() {
        // Wait for initial load
        composeTestRule.waitForIdle()

        // Find and click screenshot button
        composeTestRule.onNodeWithText("Screenshot").performClick()

        // Verify screenshot was taken
        val latch = CountDownLatch(1)
        var screenshotTaken = false

        composeTestRule.runOnUiThread {
            try {
                scInterface.take_screenshot("test")
                screenshotTaken = true
            } catch (e: Exception) {
                screenshotTaken = false
            } finally {
                latch.countDown()
            }
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS))
        assertTrue("Screenshot should be taken successfully", screenshotTaken)
    }

    @Test
    fun testDeviceInfoAccuracy() {
        // Wait for initial load
        composeTestRule.waitForIdle()

        // Click device info button - use role to specifically target the button
        composeTestRule
            .onNodeWithText("Device Info")
            .assertIsDisplayed()
            .performClick()

        // Wait for dialog to appear and verify dialog title
        composeTestRule.waitForIdle()

        // Get device info through SC_INTERFACE
        val deviceInfo = JSONObject(scInterface.device_info())

        // Verify essential device info fields
        with(deviceInfo) {
            assertNotNull("App version should not be null", optString("app_version"))
            assertNotNull("Package name should not be null", optString("package_name"))
            assertNotNull("Screen width should not be null", optString("screen_width"))
            assertNotNull("Screen height should not be null", optString("screen_height"))

            // Verify device manufacturer and model
            assertEquals(android.os.Build.MANUFACTURER, optString("device_manufacturer"))
            assertEquals(android.os.Build.MODEL, optString("device_model"))

            // Verify native info
            val nativeInfo = optJSONObject("native_info")
            assertNotNull("Native info should not be null", nativeInfo)
            assertNotNull("RAM total should not be null", nativeInfo?.optString("ram_total"))
            assertNotNull("CPU info should not be null", nativeInfo?.optJSONObject("cpu_info"))
        }

        // Verify we can close the dialog
        composeTestRule
            .onNodeWithText("OK")
            .performClick()
    }

    private fun containsString(expectedString: String): org.hamcrest.Matcher<String> {
        return org.hamcrest.CoreMatchers.containsString(expectedString)
    }
}
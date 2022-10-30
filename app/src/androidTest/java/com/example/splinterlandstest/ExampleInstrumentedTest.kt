package com.example.splinterlandstest

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileOutputStream

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ComposeScreenshots {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()

        // Clear files folder
        InstrumentationRegistry.getInstrumentation().targetContext.filesDir.listFiles()?.forEach {
            it.deleteRecursively()
        }

        // Add players
        val cache = Cache()
        cache.writePlayerToList(context, "taug")
        cache.writePlayerToList(context, "arschibald")
        cache.writePlayerToList(context, "cryptoreaper")
        cache.writePlayerToList(context, "calynn")
        cache.writePlayerToList(context, "grosh")
        cache.writePlayerToList(context, "jacekw")

        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun takePhoneScreenshots() {

        // TODO: mock api responses
        runBlocking {
            repeat(10) {
                delay(500L)
                composeTestRule.waitForIdle()
            }
        }

        // Login
        composeTestRule.takeScreenshot("screen-1.png")

        // composeTestRule.onNodeWithText("ARSCHIBALD").performClick()
    }

    private fun ComposeTestRule.takeScreenshot(file: String) {
        // TODO: Find better way to wait for animations to finish
        runBlocking {
            delay(1000L)
        }
        onRoot()
            .captureToImage()
            .asAndroidBitmap()
            .save(file)
    }

    private fun Bitmap.save(file: String) {
        val path = InstrumentationRegistry.getInstrumentation().targetContext.filesDir.canonicalPath
        FileOutputStream("$path/$file").use { out ->
            compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

}
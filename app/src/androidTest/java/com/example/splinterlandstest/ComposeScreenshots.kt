package com.example.splinterlandstest

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.screenshot.Screenshot
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
import java.io.IOException

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
        val cache = Cache(context)
        cache.writePlayerToList("taug")
        cache.writePlayerToList("arschibald")
        cache.writePlayerToList("cryptoreaper")
        cache.writePlayerToList("calynn")
        cache.writePlayerToList("grosh")
        cache.writePlayerToList("hellslash")

        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun takePhoneScreenshots() {

        // Login
        waitForContent()

        composeTestRule.takeScreenshot("screen-4.png")


        // Battles
        composeTestRule.onNodeWithText("hellslash", ignoreCase = true).performClick()

        waitForContent()

        composeTestRule.takeScreenshot("screen-1.png")


        // Collection
        onView(withId(R.id.collection)).perform(click())

        runBlocking {
            delay(5000L)
        }
        waitForContent()

        composeTestRule.takeScreenshot("screen-2.png")


        // Collection filter
        composeTestRule.onNodeWithContentDescription("FAB").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.takeScreenshot("screen-3.png")

        Espresso.pressBack()

        // Balances
        onView(withId(R.id.balances)).perform(click())

        composeTestRule.waitForIdle()

        composeTestRule.takeScreenshot("screen-5.png")

        // Rewards
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        runBlocking {
            delay(3_000L)
        }
        waitForContent()

        onView(withText("Rewards")).perform(click())

        composeTestRule.waitForIdle()

        composeTestRule.takeScreenshot("screen-6.png")

        // Rulesets
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        onView(withText("Rulesets")).perform(click())

        composeTestRule.waitForIdle()

        composeTestRule.takeScreenshot("screen-7.png")

        // Focuses
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        onView(withText("Focuses")).perform(click())

        composeTestRule.waitForIdle()

        composeTestRule.takeScreenshot("screen-8.png")
    }

    private fun waitForContent() {
        // TODO: mock api responses
        runBlocking {
            repeat(10) {
                delay(500L)
                composeTestRule.waitForIdle()
            }
        }
    }

    private fun ComposeTestRule.takeScreenshot(file: String) {
        // TODO: Find better way to wait for animations to finish
        runBlocking {
            delay(1_000L)
        }

        onAllNodes(isRoot())[0]
            .captureToImage()
            .asAndroidBitmap()
            .save(file)
    }

    private fun Bitmap.save(file: String) {
        val path = getInstrumentation().targetContext.filesDir.canonicalPath
        FileOutputStream("$path/$file").use { out ->
            compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }
}
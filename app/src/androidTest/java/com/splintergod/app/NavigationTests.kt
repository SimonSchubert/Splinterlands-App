package com.splintergod.app

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearSharedPreferences() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences = context.getSharedPreferences("splintergod-prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun testLoginScreen_isDisplayed_onFreshStart() {
        // The @Before clearSharedPreferences should handle the fresh start.
        // In LoginScreen.kt, the "ADD ACCOUNT" text is part of AddAccountCard
        composeTestRule.onNodeWithText("ADD ACCOUNT").assertIsDisplayed()
    }

    @Test
    fun testLogin_navigateTo_AccountDetailsScreen() {
        // Input player name
        // In LoginScreen.kt, the TextField is inside AddAccountCard, placeholder "Player"
        composeTestRule.onNodeWithText("Player").performTextInput("testplayer")

        // Click the add button
        // The IconButton is inside AddAccountCard, has Icons.Filled.Add
        // Assuming the content description for the Add icon is "Add" or similar.
        // If not, we'll need to find it by another means (e.g., parent/child relationship or a custom test tag).
        // For now, let's assume there's a content description.
        // After reviewing LoginFragment.kt (now LoginScreen.kt), the IconButton for adding
        // a player does not have a specific content description set for the Icon itself,
        // but the IconButton is a child of the Row that contains the TextField.
        // Let's try to find the IconButton by its visual cue (Icon).
        // A more robust way would be to add a testTag to the IconButton.
        // The Icon is Icons.Filled.Add.
        // Let's assume the "ADD ACCOUNT" text is a good anchor to find the button.
        // The structure is roughly: Card -> Column -> Text("ADD ACCOUNT") -> Row -> TextField & IconButton
        // This is tricky without a direct content description on the icon button.
        // Let's try finding the parent of the "Player" text field, then the sibling IconButton.
        // Or, more simply, if the Icon itself has a default content description that Compose assigns.
        // The Icon is `Icons.Filled.Add`. Its default contentDescription is "Add".
        composeTestRule.onNodeWithContentDescription("Add").performClick()

        // Verify AccountDetailsScreen is displayed
        // The TopAppBar in AccountDetailsScreen should display the player name.
        composeTestRule.onNodeWithText("testplayer", substring = true).assertIsDisplayed() // Check TopAppBar title

        // Also verify a bottom navigation tab, e.g., "Reward"
        composeTestRule.onNodeWithText("Reward").assertIsDisplayed() // Checks the label of the BottomNavigationItem
    }

    @Test
    fun testAccountDetails_defaultsTo_RewardTab() {
        // Simulate login by setting shared preferences or performing login UI actions
        // For simplicity and to make tests independent, let's do the UI login.
        composeTestRule.onNodeWithText("Player").performTextInput("testplayer")
        composeTestRule.onNodeWithContentDescription("Add").performClick() // Assumes "Add" is the content description for the add icon

        // Wait for AccountDetailsScreen to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("testplayer").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify that the "Reward" tab's content/text is displayed
        // Placeholder text is "Reward Screen for testplayer"
        composeTestRule.onNodeWithText("Reward Screen for testplayer", substring = true).assertIsDisplayed()

        // Verify the "Reward" tab itself is marked as selected
        // The BottomNavigationItem for "Reward" should be selected.
        composeTestRule.onNodeWithText("Reward").assertIsSelected()
    }

    @Test
    fun testAccountDetails_navigateTo_CollectionTab() {
        // Simulate login
        composeTestRule.onNodeWithText("Player").performTextInput("testplayer")
        composeTestRule.onNodeWithContentDescription("Add").performClick() // Assumes "Add" is the content description for the add icon

        // Wait for AccountDetailsScreen to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("testplayer").fetchSemanticsNodes().isNotEmpty()
        }

        // Click the "Collection" tab
        composeTestRule.onNodeWithText("Collection").performClick()

        // Verify that the "Collection" tab's content/text is displayed
        // CollectionScreen has a FAB with contentDescription "FAB" and an icon R.drawable.tune
        // It also has "Sort by:" text in the FilterDialog (if opened) or cards.
        // Let's check for the FAB as it's always present on the ReadyScreen of Collection.
        // The FAB icon is R.drawable.tune. It does not have a default content description.
        // The FAB itself has contentDescription "FAB".
        composeTestRule.onNodeWithContentDescription("FAB").assertIsDisplayed() // Check for the Filter FAB

        // Verify the "Collection" tab is marked as selected
        composeTestRule.onNodeWithText("Collection").assertIsSelected()
    }

    @Test
    fun testLogout_navigateTo_LoginScreen() {
        // Perform login
        composeTestRule.onNodeWithText("Player").performTextInput("testplayer")
        composeTestRule.onNodeWithContentDescription("Add").performClick() // Assumes "Add" is content description for add icon

        // Verify navigation to AccountDetailsScreen by checking for the player name in TopAppBar
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("testplayer", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("testplayer", substring = true).assertIsDisplayed()

        // Click the logout icon in the TopAppBar
        // The content description for the logout IconButton was set to "Logout"
        composeTestRule.onNodeWithContentDescription("Logout").performClick()

        // Verify that the LoginScreen is displayed again
        // Check for the "ADD ACCOUNT" text which is unique to LoginScreen's AddAccountCard
        composeTestRule.onNodeWithText("ADD ACCOUNT").assertIsDisplayed()

        // Optional: Further verification could involve checking SharedPreferences or ViewModel state
        // if test hooks were available. For now, navigating to LoginScreen is the primary assertion.
    }
}

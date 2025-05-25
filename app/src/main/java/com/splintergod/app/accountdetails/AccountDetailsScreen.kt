package com.splintergod.app.accountdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.splintergod.app.MainActivityViewModel
import com.splintergod.app.R
import com.splintergod.app.collection.CollectionScreen
import com.splintergod.app.rewards.RewardScreen
import org.koin.androidx.compose.koinViewModel

sealed class AccountDetailsScreenTabs(
    val route: String,
    val label: String,
    val iconResId: Int,
    val contentDescription: String
) {
    data object Collection :
        AccountDetailsScreenTabs("collection", "Collection", R.drawable.collection, "Collection")

    data object Reward : AccountDetailsScreenTabs("reward", "Reward", R.drawable.chest, "Reward")
}

val accountDetailsTabs = listOf(
    AccountDetailsScreenTabs.Collection,
    AccountDetailsScreenTabs.Reward
)

@Composable
fun AccountDetailsScreen(
    navController: NavHostController, // Main NavController
    playerName: String,
    mainActivityViewModel: MainActivityViewModel = koinViewModel()
) {
    val innerNavController = rememberNavController() // NavController for inner tabs

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playerName) },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = {
                        mainActivityViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }

                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            navController.navigate("rulesets")
                            showMenu = false
                        }) {
                            Text("Rulesets")
                        }
                        DropdownMenuItem(onClick = {
                            navController.navigate("abilities")
                            showMenu = false
                        }) {
                            Text("Abilities")
                        }
                        DropdownMenuItem(onClick = {
                            navController.navigate("balances")
                            showMenu = false
                        }) {
                            Text("Balances")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                accountDetailsTabs.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painterResource(id = screen.iconResId),
                                modifier = Modifier.size(32.dp),
                                contentDescription = screen.contentDescription
                            )
                        },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            innerNavController.navigate(screen.route) {
                                popUpTo(innerNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = AccountDetailsScreenTabs.Reward.route, // Default to Reward tab
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AccountDetailsScreenTabs.Collection.route) {
                CollectionScreen(navController = navController, viewModel = koinViewModel())
            }
            composable(AccountDetailsScreenTabs.Reward.route) {
                RewardScreen(navController = navController, viewModel = koinViewModel())
            }
        }
    }
}

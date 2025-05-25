package com.splintergod.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.splintergod.app.accountdetails.AccountDetailsScreen
import com.splintergod.app.carddetail.CardDetailScreen
import com.splintergod.app.focuses.FocusesScreen
import com.splintergod.app.abilities.AbilitiesScreen
import com.splintergod.app.login.LoginScreen
import com.splintergod.app.balances.BalancesScreen
import com.splintergod.app.login.LoginViewModel
import com.splintergod.app.rulesets.RulesetsScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val startDestination = if (viewModel.isLoggedIn()) {
                "account_details/${viewModel.getPlayerName()}"
            } else {
                "login"
            }

            NavHost(navController = navController, startDestination = startDestination) {
                composable("login") {
                    val loginViewModel: LoginViewModel = koinViewModel()
                    LoginScreen(
                        navController = navController,
                        viewModel = loginViewModel,
                        state = loginViewModel.state.collectAsState().value,
                        onClickPlayer = { player ->
                            navController.navigate("account_details/$player")
                        }
                    )
                }
                composable(
                    route = "account_details/{playerName}",
                    arguments = listOf(navArgument("playerName") { type = NavType.StringType })
                ) { backStackEntry ->
                    val playerName = backStackEntry.arguments?.getString("playerName")
                    if (playerName != null) {
                        AccountDetailsScreen(navController = navController, playerName = playerName)
                    } else {
                        // Handle error: playerName is null
                        Text("Error: Player name not found")
                    }
                }
                composable(
                    route = "card_detail/{cardId}/{level}",
                    arguments = listOf(
                        navArgument("cardId") { type = NavType.StringType },
                        navArgument("level") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val cardId = backStackEntry.arguments?.getString("cardId") ?: ""
                    val levelString = backStackEntry.arguments?.getString("level") ?: "1"
                    CardDetailScreen(navController = navController, cardId = cardId, levelString = levelString)
                }
                composable("focuses") {
                    FocusesScreen(navController = navController)
                }
                composable("rulesets") {
                    RulesetsScreen(navController = navController)
                }
                composable("abilities") {
                    AbilitiesScreen(navController = navController)
                }
                composable("balances") {
                    BalancesScreen(navController = navController)
                }
            }
        }
    }
}

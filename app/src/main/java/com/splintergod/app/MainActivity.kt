package com.splintergod.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.splinterlandstest.R
import com.example.splinterlandstest.databinding.ActivityMainBinding
import com.splintergod.app.abilities.AbilitiesFragment
import com.splintergod.app.balances.BalancesFragment
import com.splintergod.app.battles.BattlesFragment
import com.splintergod.app.collection.CollectionFragment
import com.splintergod.app.focuses.FocusesFragment
import com.splintergod.app.login.LoginFragment
import com.splintergod.app.rewards.RewardsFragment
import com.splintergod.app.rulesets.RulesetsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.battles -> setCurrentFragment(BattlesFragment())
                R.id.collection -> setCurrentFragment(CollectionFragment())
                R.id.balances -> setCurrentFragment(BalancesFragment())
            }
            true
        }

        if (savedInstanceState == null || !viewModel.isInitialized) {
            viewModel.init()
            if (viewModel.isLoggedIn()) {
                setCurrentFragment(BattlesFragment())
            } else {
                binding.bottomNavigation.isVisible = false
                setCurrentFragment(LoginFragment())
                invalidateOptionsMenu()
            }
        }

        viewModel.loginStatus.observe(this) {
            if (it) {
                binding.bottomNavigation.isVisible = true
                binding.bottomNavigation.selectedItemId = R.id.battles
                invalidateOptionsMenu()
            } else {
                binding.bottomNavigation.isVisible = false
                setCurrentFragment(LoginFragment())
                invalidateOptionsMenu()
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.include, fragment)
            commit()
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.menu_rewards).isVisible = viewModel.isLoggedIn()
        menu.findItem(R.id.menu_focuses).isVisible = viewModel.isLoggedIn()
        menu.findItem(R.id.menu_rulesets).isVisible = viewModel.isLoggedIn()
        menu.findItem(R.id.menu_abilities).isVisible = viewModel.isLoggedIn()
        menu.findItem(R.id.menu_logout).isVisible = viewModel.isLoggedIn()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_rewards -> {
                setCurrentFragment(RewardsFragment())
                return true
            }
            R.id.menu_focuses -> {
                setCurrentFragment(FocusesFragment())
                return true
            }
            R.id.menu_rulesets -> {
                setCurrentFragment(RulesetsFragment())
                return true
            }
            R.id.menu_abilities -> {
                setCurrentFragment(AbilitiesFragment())
                return true
            }
            R.id.menu_logout -> {
                viewModel.logout()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

@Composable
fun Main() {
//
//    val navController = rememberNavController()
//    Scaffold(
//        bottomBar = {
//            BottomNavigation {
//                val navBackStackEntry by navController.currentBackStackEntryAsState()
//                val currentDestination = navBackStackEntry?.destination
//                items.forEach { screen ->
//                    BottomNavigationItem(
//                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
//                        label = { Text(stringResource(screen.resourceId)) },
//                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
//                        onClick = {
//                            navController.navigate(screen.route) {
//                                // Pop up to the start destination of the graph to
//                                // avoid building up a large stack of destinations
//                                // on the back stack as users select items
//                                popUpTo(navController.graph.findStartDestination().id) {
//                                    saveState = true
//                                }
//                                // Avoid multiple copies of the same destination when
//                                // reselecting the same item
//                                launchSingleTop = true
//                                // Restore state when reselecting a previously selected item
//                                restoreState = true
//                            }
//                        }
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        NavHost(navController, startDestination = Screen.Profile.route, Modifier.padding(innerPadding)) {
//            composable(Screen.Profile.route) { Profile(navController) }
//            composable(Screen.FriendsList.route) { FriendsList(navController) }
//        }
//    }
}

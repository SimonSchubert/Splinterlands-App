package com.splintergod.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.splintergod.app.abilities.AbilitiesFragment
import com.splintergod.app.balances.BalancesFragment
import com.splintergod.app.battles.BattlesFragment
import com.splintergod.app.collection.CollectionFragment
import com.splintergod.app.databinding.ActivityMainBinding
import com.splintergod.app.focuses.FocusesFragment
import com.splintergod.app.login.LoginFragment
import com.splintergod.app.rewards.RewardsFragment
import com.splintergod.app.rulesets.RulesetsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

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
            viewModel.init {
                binding.bottomNavigation.isVisible = true
                binding.bottomNavigation.selectedItemId = R.id.battles
            }
            if (viewModel.isLoggedIn()) {
                setCurrentFragment(BattlesFragment())
            } else {
                setCurrentFragment(LoginFragment())
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.include)
            val homeAsUpEnabled =
                fragment !is LoginFragment && fragment !is BattlesFragment && fragment !is CollectionFragment && fragment !is BalancesFragment
            supportActionBar?.setDisplayHomeAsUpEnabled(homeAsUpEnabled)
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        if (fragment is LoginFragment ||
            fragment is BattlesFragment ||
            fragment is CollectionFragment ||
            fragment is BalancesFragment
        ) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        supportFragmentManager.popBackStack(fragment.javaClass.simpleName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.include, fragment, fragment.javaClass.simpleName)
            addToBackStack(fragment.javaClass.simpleName)
            commit()
        }
        if (fragment is LoginFragment) {
            binding.bottomNavigation.isVisible = false
        } else if (fragment is BattlesFragment) {
            binding.bottomNavigation.isVisible = true
        }
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.menu_logout).isVisible = viewModel.isLoggedIn()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFragmentManager.popBackStack()
                return true
            }
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
                setCurrentFragment(LoginFragment())
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
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

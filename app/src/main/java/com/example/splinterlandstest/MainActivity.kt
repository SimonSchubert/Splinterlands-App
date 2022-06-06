package com.example.splinterlandstest

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.splinterlandstest.balances.BalancesFragment
import com.example.splinterlandstest.battles.BattlesFragment
import com.example.splinterlandstest.collection.CollectionFragment
import com.example.splinterlandstest.databinding.ActivityMainBinding
import com.example.splinterlandstest.login.LoginFragment

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.battles -> setCurrentFragment(BattlesFragment())
                R.id.collection -> setCurrentFragment(CollectionFragment())
                R.id.balances -> setCurrentFragment(BalancesFragment())
            }
            true
        }

        if (savedInstanceState == null) {
            viewModel.init(this)
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
                setCurrentFragment(BattlesFragment())
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
        menu.findItem(R.id.menu_logout).isVisible = viewModel.isLoggedIn()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.logout(this)
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
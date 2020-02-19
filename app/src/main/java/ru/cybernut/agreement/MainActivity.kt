package ru.cybernut.agreement

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import ru.cybernut.agreement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navController = this.findNavController(R.id.nav_host)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.serviceRequestListFragment, R.id.requestListFragment, R.id.deliveryRequestListFragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.setupWithNavController(navController)
        binding.bottomNavView.setupWithNavController(navController)
//        binding.bottomNavView.setOnNavigationItemSelectedListener { item: MenuItem ->
//            return@setOnNavigationItemSelectedListener when (item.itemId) {
//                R.id.deliveryRequestListFragment1 -> {
//                    Toast.makeText(
//                        this,
//                        "deliveryRequestListFragment1!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    val builder = NavOptions.Builder()
//                    var navOptions = builder.setPopUpTo(R.id.deliveryRequestListFragment, true).build()
//                    navController.navigate(R.id.deliveryRequestListFragment)
//                    true
//                }
//                R.id.serviceRequestListFragment1 -> {
//                    Toast.makeText(
//                        this,
//                        "serviceRequestListFragment1!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    navController.navigate(R.id.serviceRequestListFragment)
//                    true
//                }
//                R.id.requestListFragment1 -> {
//                    Toast.makeText(
//                        this,
//                        "requestListFragment1!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    navController.navigate(R.id.requestListFragment)
//                    true
//                }
//                else -> false
//            }
//        }

        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, bundle: Bundle? ->
            if (nd.id == R.id.requestListFragment || nd.id == R.id.serviceRequestListFragment || nd.id == R.id.deliveryRequestListFragment) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host)
        return navController.navigateUp(appBarConfiguration)
        //return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu);
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.nav_host))
                || super.onOptionsItemSelected(item)
    }
}

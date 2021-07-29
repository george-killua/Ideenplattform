package com.killua.ideenplattform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.killua.ideenplattform.databinding.ActivityMainBinding
import com.killua.ideenplattform.ui.home.HomeFragment
import com.killua.ideenplattform.ui.profile.ProfileFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_top_ranked, R.id.navigation_home, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
     /*   val navigation = binding.navigation
        loadFragment(HomeFragment.newInstance(false))
        navigation.menu.getItem(1).isChecked = true
        navigation.setOnItemSelectedListener {
            var fragment: Fragment? = null

            when (it.itemId) {
                R.id.navigation_home -> {
                    fragment = HomeFragment.newInstance(false)
                }
                R.id.navigation_top_ranked -> {
                    fragment = HomeFragment.newInstance(true)
                }
                R.id.navigation_profile -> fragment = ProfileFragment()
            }

            return@setOnItemSelectedListener loadFragment(fragment)

        }*/
    }

    override fun onSupportNavigateUp()=findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.scale_up,
                R.anim.scale_down
            )
           // transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
            return true
        }
        return false
    }


}
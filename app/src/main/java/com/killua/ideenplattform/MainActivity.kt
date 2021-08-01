package com.killua.ideenplattform

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.killua.ideenplattform.databinding.ActivityMainBinding
import com.killua.ideenplattform.ui.uiutils.UIBasementController


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val (navView: BottomNavigationView, navController) = setupNavController()

        setUpToolbar(navController, navView)

    }

    private fun setupNavController(): Pair<BottomNavigationView, NavController> {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return Pair(navView, navController)
    }

    private fun setUpToolbar(
        navController: NavController,
        navView: BottomNavigationView,
    ) {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_top_ranked, R.id.navigation_home, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
package com.killua.ideenplattform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.killua.ideenplattform.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //testing
    // val repository: MainRepository by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //testing
        /*    runBlocking {
                repository.login("max.mustermann@example.org", "supersecurepassword1234").collect {
                    if (it.data == true) Log.e("george", it.networkErrorMessage ?: "success")
                }


            } */
        //   val navView: BottomNavigationView = binding.navView

        //   val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //  val appBarConfiguration = AppBarConfiguration(
        // setOf(
        //     R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
        //      )
        //  )
        //  setupActionBarWithNavController(navController, appBarConfiguration)
        //    navView.setupWithNavController(navController)
    }
}
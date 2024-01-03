package com.mariejuana.flavorfusion

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth : FirebaseAuth

    private var database = RealmDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializes the credentials of the current user
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Initializes the variables / binding needed for the nav header
        val headerView = navView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.txt_user_name)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.txt_user_email)
        val buttonLogoutUser = headerView.findViewById<Button>(R.id.btn_logout)

        // Initializes the user's email from Firebase and name coming from the Realm database
        val currentUserEmail = currentUser?.email
        val currentUserName = database.getCurrentUserName(currentUserEmail.toString())

        // Places the initialized variables for name and email of the current user
        userNameTextView.text = currentUserName
        userEmailTextView.text = currentUserEmail

        // Sets the logout of the user from the application using the Firebase's signOut function
        buttonLogoutUser.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("Are you sure you want to logout?")
            builder.setTitle("Warning!")
            builder.setPositiveButton("Yes") { dialog, _ ->
                auth.signOut()
                finish()
                Toast.makeText(this@MainActivity, "Logged out successfully.", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("No") {dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
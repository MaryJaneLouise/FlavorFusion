package com.mariejuana.flavorfusion.ui.screens.account.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.MainActivity
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.databinding.ActivityLoginBinding
import com.mariejuana.flavorfusion.ui.screens.account.dialogs.ForgotUserPasswordDialog
import com.mariejuana.flavorfusion.ui.screens.account.login.LoginActivity
import com.mariejuana.flavorfusion.ui.screens.account.register.RegisterActivity
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private var database = RealmDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializes the Firebase authentication then gets the information about the current user
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Checks if the user is not returning a null value then goes to the main activity
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }

        with(binding){
            btnLogin.setOnClickListener{
                // These if statements check the respective fields if they are null / blank / empty
                if (edtEmail.editText?.text.isNullOrEmpty()) {
                    edtEmail.error = "Required"
                    return@setOnClickListener
                }

                if (edtPassword.editText?.text.isNullOrEmpty()) {
                    edtPassword.error = "Required"
                    return@setOnClickListener
                }

                // Shows the indicator if it success
                progressIndicator.visibility = View.VISIBLE
                it.isEnabled = false

                // Converts the necessary fields into a string
                val userEmail = edtEmail.editText?.text.toString()
                val userPass = edtPassword.editText?.text.toString()

                // Passes the converted string to the register function
                login(userEmail, userPass)
            }

            // This activates the forgot password by sending the email to the firebase
            btnForgot.setOnClickListener {
                val forgotPassword = ForgotUserPasswordDialog()
                forgotPassword.show(supportFragmentManager, null)
            }

            // Goes to the register activity of the app
            txtRegister.setOnClickListener{
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Logs in the user for both Firebase and Realm database in order to have "fusion" together in
    // accessing data for the user
    private fun login(email: String, password: String) {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("addUserToRealm"))

        // Uses the function of the Firebase in order to add the user being logged in
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Clears the fields when successfully logged in on the app
                    with(binding) {
                        btnLogin.isEnabled = true
                        progressIndicator.visibility = View.GONE

                        edtEmail.editText?.text?.clear()
                        edtPassword.editText?.text?.clear()
                    }

                    // Initializes the Shared Preferences in order to use it to many transaction especially for the CRUD purposes
                    // It saves the email as the id of the current user
                    val sharedPref = this@LoginActivity.getSharedPreferences("username_login", Context.MODE_PRIVATE)
                    val editor = sharedPref?.edit()
                    editor?.putString("username", email)
                    editor?.apply()

                    // This code checks if the user is already existing in the Realm database
                    // If not, it will create a new user and add the user to the Realm database
                    scope.launch(Dispatchers.IO) {
                        val currentUserName = database.getCurrentUserName(email)

                        if (currentUserName == "" || currentUserName == null) {
                            val name = email.substringBefore("@")
                            database.addUser(name, email, password)
                        }

                        // Updates the password of the user once entered the application
                        database.updatePassword(email, password)
                    }

                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
            }.addOnFailureListener {
                // This shows the error for registering the user
                binding.btnLogin.isEnabled = true
                binding.progressIndicator.visibility = View.GONE
                Toast.makeText(this,"Error: " + it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
}
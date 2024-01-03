package com.mariejuana.flavorfusion.ui.screens.account.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth

    private var database = RealmDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializes the Firebase authentication
        auth = FirebaseAuth.getInstance()

        with(binding){
            btnRegister.setOnClickListener{
                // These if statements check the respective fields if they are null / blank / empty
                if(edtName.text.isNullOrEmpty()){
                    edtName.error = "Required"
                    return@setOnClickListener
                }

                if(edtEmail.text.isNullOrEmpty()){
                    edtEmail.error = "Required"
                    return@setOnClickListener
                }

                if(edtPassword.text.length < 6){
                    edtPassword.error = "Must be greater than or equal to 6 characters."
                    return@setOnClickListener
                }

                if(edtPassword.text.toString() != edtRepeatPassword.text.toString()){
                    edtPassword.error = "Password does not match"
                    edtRepeatPassword.error = "Password does not match"
                    return@setOnClickListener
                }

                // Shows the indicator if it success
                progressIndicator.visibility = View.VISIBLE
                it.isEnabled = false

                // Converts the necessary fields into a string
                val userName = edtName.text.toString()
                val userEmail = edtEmail.text.toString()
                val userPassword = edtPassword.text.toString()

                // Passes the converted string to the register function
                register(userName, userEmail, userPassword)
            }
        }
    }

    // Registers the user for both Firebase and Realm database in order to have "fusion" together in
    // accessing data for the user
    private fun register(name: String, email: String, password: String) {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("addUserToRealm"))

        scope.launch(Dispatchers.IO) {
            // Uses the function of the Firebase in order to add the user being registered
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Adds the user to the Realm database
                        scope.launch(Dispatchers.IO) { database.addUser(name, email, password) }

                        Toast.makeText(this@RegisterActivity,"Successfully registered", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }.addOnFailureListener { exception ->
                    // This shows the error for registering the user
                    binding.btnRegister.isEnabled = true
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this@RegisterActivity,"Error: " + exception.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
    }
}
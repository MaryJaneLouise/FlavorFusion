package com.mariejuana.flavorfusion.ui.screens.account.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.MainActivity
import com.mariejuana.flavorfusion.databinding.ActivityLoginBinding
import com.mariejuana.flavorfusion.ui.screens.account.login.LoginActivity
import com.mariejuana.flavorfusion.ui.screens.account.register.RegisterActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }

        with(binding){
            btnLogin.setOnClickListener{
                if(edtEmail.text.isNullOrEmpty()){
                    edtEmail.error = "Required"
                    return@setOnClickListener
                }

                if(edtPassword.text.isNullOrEmpty()){
                    edtPassword.error = "Required"
                    return@setOnClickListener
                }

                progressIndicator.visibility = View.VISIBLE

                it.isEnabled = false
                login(edtEmail.text.toString(),edtPassword.text.toString())
            }

            txtRegister.setOnClickListener{
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    binding.btnLogin.isEnabled = true
                    binding.progressIndicator.visibility = View.GONE
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
            }.addOnFailureListener { exception ->
                binding.btnLogin.isEnabled = true
                binding.progressIndicator.visibility = View.GONE
                Toast.makeText(this,"Error: " + exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
}
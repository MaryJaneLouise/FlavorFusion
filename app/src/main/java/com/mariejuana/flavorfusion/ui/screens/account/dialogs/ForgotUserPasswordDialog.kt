package com.mariejuana.flavorfusion.ui.screens.account.dialogs

import android.R
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.data.helpers.queries.CategoryAllQuery
import com.mariejuana.flavorfusion.data.helpers.retrofit.RetrofitHelper
import com.mariejuana.flavorfusion.databinding.DialogAddCustomMealBinding
import com.mariejuana.flavorfusion.databinding.DialogForgotPasswordBinding
import com.mariejuana.flavorfusion.databinding.DialogUpdateAccountPasswordBinding
import com.mariejuana.flavorfusion.databinding.DialogUpdateCustomMealBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.create

class ForgotUserPasswordDialog : DialogFragment() {
    private lateinit var binding: DialogForgotPasswordBinding
    private lateinit var auth : FirebaseAuth

    // Initializes the layout style of the dialog fragment
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogForgotPasswordBinding.inflate(layoutInflater,container,false)

        // Initializes the Firebase authentication
        auth = Firebase.auth

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // Sends the email to the Firebase
            btnSendEmail.setOnClickListener {
                // These if statements check the respective fields if they are null / blank / empty
                if (edtUserEmail.text.isNullOrEmpty()) {
                    edtUserEmail.error = "Required"
                    return@setOnClickListener
                }

                // Converts the necessary fields into a string
                val forgotEmail = edtUserEmail.text.toString()

                // Passes the converted string to the register function
                resetEmailPassword(forgotEmail)
            }

            // Makes the dialog cancel
            btnCancel.setOnClickListener {
                dialog?.cancel()
            }
        }
    }

    // Sends the email entered by the user to the Firebase
    private fun resetEmailPassword(forgotEmail: String) {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("forgotPasswordUser"))

        scope.launch(Dispatchers.IO) {
            // Uses the function for sending an reset password email from Firebase
            Firebase.auth.sendPasswordResetEmail(forgotEmail)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(),"Successfully sent email.", Toast.LENGTH_SHORT).show()
                        dialog?.dismiss()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),"Error: " + it.localizedMessage,Toast.LENGTH_SHORT).show()
                }
        }
    }
}
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
import com.mariejuana.flavorfusion.databinding.DialogUpdateAccountPasswordBinding
import com.mariejuana.flavorfusion.databinding.DialogUpdateCustomMealBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.create

class EditUserPasswordDialog : DialogFragment() {
    private lateinit var binding: DialogUpdateAccountPasswordBinding
    private lateinit var auth : FirebaseAuth

    private var database = RealmDatabase()

    // Initializes the layout style of the dialog fragment
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogUpdateAccountPasswordBinding.inflate(layoutInflater,container,false)

        // Initializes the Firebase authentication
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // Updates the password of the user
            btnUpdate.setOnClickListener {
                // These if statements check the respective fields if they are null / blank / empty
                if (edtUserOldPassword.text.isNullOrEmpty()) {
                    edtUserOldPassword.error = "Required"
                    return@setOnClickListener
                }

                if (edtUserNewPassword.text.isNullOrEmpty()) {
                    edtUserNewPassword.error = "Required"
                    return@setOnClickListener
                }

                if (edtUserRetypeNewPassword.text.isNullOrEmpty()) {
                    edtUserRetypeNewPassword.error = "Required"
                    return@setOnClickListener
                }

                if (edtUserNewPassword.text.length < 6) {
                    edtUserNewPassword.error = "Must be greater than or equal to 6 characters."
                    return@setOnClickListener
                }

                if (edtUserNewPassword.text.toString() != edtUserRetypeNewPassword.text.toString()) {
                    edtUserNewPassword.error = "Required"
                    edtUserRetypeNewPassword.error = "Required"
                } else {
                    // Converts the necessary fields into a string
                    val oldPassword = edtUserOldPassword.text.toString()
                    val newPassword = edtUserNewPassword.text.toString()

                    // Passes the converted string to the register function
                    updatePassword(oldPassword, newPassword)
                }
            }

            // Makes the dialog cancel
            btnCancel.setOnClickListener {
                dialog?.cancel()
            }
        }
    }

    private fun updatePassword(oldPassword: String, newPassword: String) {
        // Uses the Shared Preferences in order to share the email into other fragments or activity
        val sharedPref = context?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("updatePasswordUser"))
        scope.launch(Dispatchers.IO) {
            // Initializes the re-authentication for the Firebase since it is required for it to recheck the details
            val currentUserEmail = auth.currentUser?.email.toString()
            val credential = EmailAuthProvider.getCredential(currentUserEmail, oldPassword)

            // Re-authenticates the user being logged in
            auth.currentUser?.reauthenticate(credential)
                ?.addOnCompleteListener {
                    // Updates the password to the Firebase
                    auth.currentUser!!.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Updates the password to the Realm database
                                scope.launch(Dispatchers.IO) {
                                    if (username != null) {
                                        database.updatePassword(username, newPassword)
                                    }
                                }
                                Toast.makeText(requireContext(),"Successfully changed password.", Toast.LENGTH_SHORT).show()
                                dialog?.dismiss()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),"Error: " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                }!!.addOnFailureListener {
                    Toast.makeText(requireContext(),"Error: " + it.localizedMessage,Toast.LENGTH_SHORT).show()
                }
        }
    }
}
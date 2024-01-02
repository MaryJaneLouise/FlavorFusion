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


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogUpdateAccountPasswordBinding.inflate(layoutInflater,container,false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        with(binding) {
            btnUpdate.setOnClickListener {
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
                }

                val newPassword = edtUserNewPassword.text.toString()
                updatePassword(newPassword)
            }

            // Makes the dialog cancel
            btnCancel.setOnClickListener {
                dialog?.cancel()
            }
        }
    }

    private fun updatePassword(newPassword: String) {
        val sharedPref = context?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("updatePasswordUser"))
        scope.launch(Dispatchers.IO) {
            val credential = EmailAuthProvider
                .getCredential(auth.currentUser?.email.toString(), binding.edtUserOldPassword.text.toString())
            auth.currentUser?.reauthenticate(credential)
                ?.addOnCompleteListener {
                    auth.currentUser!!.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
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
                    Toast.makeText(requireContext(),"Old Password doesn't match!",Toast.LENGTH_SHORT)
                }
        }
    }
}
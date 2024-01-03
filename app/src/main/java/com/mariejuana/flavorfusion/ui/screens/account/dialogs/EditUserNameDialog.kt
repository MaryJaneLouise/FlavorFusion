package com.mariejuana.flavorfusion.ui.screens.account.dialogs

import android.R
import android.content.Context
import android.os.Bundle
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
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.data.helpers.queries.CategoryAllQuery
import com.mariejuana.flavorfusion.data.helpers.retrofit.RetrofitHelper
import com.mariejuana.flavorfusion.databinding.DialogAddCustomMealBinding
import com.mariejuana.flavorfusion.databinding.DialogUpdateAccountNameBinding
import com.mariejuana.flavorfusion.databinding.DialogUpdateCustomMealBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.create

class EditUserNameDialog : DialogFragment() {
    private lateinit var binding: DialogUpdateAccountNameBinding
    lateinit var refreshDataCallback: RefreshDataInterface

    private var database = RealmDatabase()

    // Calls the function for the fragment in order to refresh the data after the dialog has been dismissed
    interface RefreshDataInterface {
        fun refreshData()
    }

    // Initializes the layout style of the dialog fragment
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogUpdateAccountNameBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Uses the Shared Preferences in order to share the email into other fragments or activity
        // then it will locate the said email to the Realm database in order to change things
        val sharedPref = activity?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")
        val currentUserName = username?.let { database.getCurrentUserName(it) }

        with(binding) {
            // Pre-sets the current name of the user
            edtUserName.setText(currentUserName)

            // Updates the name of the user
            btnUpdate.setOnClickListener {
                // These if statements check the respective fields if they are null / blank / empty
                if (edtUserName.text.isNullOrEmpty()) {
                    edtUserName.error = "Required"
                    return@setOnClickListener
                }

                // Converts the necessary fields into a string
                val newName = edtUserName.text.toString()

                // Updates the name of the user then updates the fragment too after dismissing the dialog
                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("updateNameUser"))

                scope.launch(Dispatchers.IO) {
                    if (username != null) {
                        database.updateUserName(username, newName)
                    }
                    withContext(Dispatchers.Main) {
                        refreshDataCallback.refreshData()
                        Toast.makeText(requireContext(),"Successfully changed name.", Toast.LENGTH_SHORT).show()
                        dialog?.dismiss()
                    }
                }
            }

            // Makes the dialog cancel
            btnCancel.setOnClickListener {
                dialog?.cancel()
            }
        }
    }
}
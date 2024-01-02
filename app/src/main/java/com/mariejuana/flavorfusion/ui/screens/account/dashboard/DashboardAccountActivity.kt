package com.mariejuana.flavorfusion.ui.screens.account.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.R
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.databinding.ActivityDashboardAccountBinding
import com.mariejuana.flavorfusion.ui.screens.account.dialogs.EditUserNameDialog
import com.mariejuana.flavorfusion.ui.screens.account.dialogs.EditUserPasswordDialog

class DashboardAccountActivity : AppCompatActivity(),
    EditUserNameDialog.RefreshDataInterface {
    private lateinit var binding: ActivityDashboardAccountBinding
    private lateinit var auth : FirebaseAuth

    private var database = RealmDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentUserEmail = currentUser?.email
        val currentUserName = database.getCurrentUserName(currentUserEmail.toString())

        with(binding) {
            txtUserEmail.text = currentUserEmail
            txtUserName.text = currentUserName

            btnEditName.setOnClickListener {
                val editUserNameDialog = EditUserNameDialog()
                editUserNameDialog.refreshDataCallback = this@DashboardAccountActivity
                editUserNameDialog.show(supportFragmentManager, null)

            }

            btnEditPassword.setOnClickListener {
                val editUserPasswordDialog = EditUserPasswordDialog()
                editUserPasswordDialog.show(supportFragmentManager, null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun refreshData() {
        // nothing to do
    }

    private fun mapUser() {

    }
}
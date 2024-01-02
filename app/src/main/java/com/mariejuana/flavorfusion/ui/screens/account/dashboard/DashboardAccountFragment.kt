package com.mariejuana.flavorfusion.ui.screens.account.dashboard

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.R
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.data.database.realm.models.UserModel
import com.mariejuana.flavorfusion.data.models.users.User
import com.mariejuana.flavorfusion.databinding.FragmentDashboardAccountBinding
import com.mariejuana.flavorfusion.ui.screens.account.dialogs.EditUserNameDialog
import com.mariejuana.flavorfusion.ui.screens.account.dialogs.EditUserPasswordDialog
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardAccountFragment : Fragment(),
    EditUserNameDialog.RefreshDataInterface {
    private lateinit var binding: FragmentDashboardAccountBinding
    private lateinit var auth : FirebaseAuth

    private var database = RealmDatabase()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardAccountBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentUserEmail = currentUser?.email
        val currentUserName = database.getCurrentUserName(currentUserEmail.toString())

        with(binding) {
            txtUserEmail.text = currentUserEmail
            txtUserName.text = currentUserName

            btnEditName.setOnClickListener {
                val editUserNameDialog = EditUserNameDialog()
                val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager

                editUserNameDialog.refreshDataCallback = this@DashboardAccountFragment
                editUserNameDialog.show(manager, null)
            }

            btnEditPassword.setOnClickListener {
                val editUserPasswordDialog = EditUserPasswordDialog()
                val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                editUserPasswordDialog.show(manager, null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getNewName()
    }

    override fun refreshData() {
        getNewName()
    }

    private fun mapUser(user: UserModel): User {
        return User(
            id = user.id.toHexString(),
            name = user.name,
            username = user.username,
            password = user.password,
            favoriteFoodCount = 0
        )
    }

    private fun getNewName() {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("loadName"))
        val sharedPref = activity?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        scope.launch(Dispatchers.IO) {
            if (username != null) {
                val newName = database.getCurrentUserName(username)

                withContext(Dispatchers.Main) {
                    binding.txtUserName.text = newName
                }
            }
        }
    }

}
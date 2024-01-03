package com.mariejuana.flavorfusion.ui.screens.food.dialogs

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
import com.mariejuana.flavorfusion.databinding.DialogUpdateCustomMealBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.create

class EditCustomMealDialog : DialogFragment() {
    private lateinit var binding: DialogUpdateCustomMealBinding
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
        binding = DialogUpdateCustomMealBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Uses the Shared Preferences in order to share the email into other fragments or activity
        val sharedPref = activity?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        // Instead of using Shared Preferences, the app uses Bundle Arguments as a way of sharing the data
        // from another fragment to this dialog
        val bundle = arguments
        val mealId = bundle!!.getString("EditCustomFoodId")
        val mealName = bundle!!.getString("EditCustomFoodName")
        val mealInstruction = bundle!!.getString("EditCustomFoodInstructions")
        val mealIngredient = bundle!!.getString("EditCustomFoodIngredients")

        // Converts the variable into a string
        val editMealName = mealName.toString()
        val editMealId = mealId.toString()
        val editMealInstruction = mealInstruction.toString()
        val editMealIngredient = mealIngredient.toString()

        // Sets the editViewText to have pre-set data from the selected item
        with(binding) {
            edtMealName.setText(editMealName)
            edtMealInstructions.setText(editMealInstruction)
            edtMealIngredients.setText(editMealIngredient)
        }

        // Loads the categories for the meal coming from the API
        val categoryNamesInitiate = RetrofitHelper.getInstance().create(CategoryAllQuery::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            val resultCategory = categoryNamesInitiate.getAllCategories()
            val categoryNames = resultCategory.body()

            if (categoryNames != null) {
                withContext(Dispatchers.Main) {
                    val categoryListSpinner = categoryNames.meals.map { it.strCategory }
                    val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, categoryListSpinner)

                    with(binding) {
                        edtMealCategory.adapter = spinnerAdapter

                        btnUpdate.setOnClickListener {
                            // These if statements check the respective fields if they are null / blank / empty
                            if (edtMealName.text.isNullOrBlank() || edtMealName.text.isNullOrEmpty()) {
                                edtMealName.error = "Required"
                                return@setOnClickListener
                            }

                            if (edtMealIngredients.text.isNullOrBlank() || edtMealIngredients.text.isNullOrEmpty()) {
                                edtMealIngredients.error = "Required"
                                return@setOnClickListener
                            }

                            if (edtMealInstructions.text.isNullOrBlank() || edtMealInstructions.text.isNullOrEmpty()) {
                                edtMealInstructions.error = "Required"
                                return@setOnClickListener
                            }

                            // Converts the necessary fields into a string
                            val mealName = edtMealName.text.toString()
                            val mealIngredients = edtMealIngredients.text.toString()
                            val mealInstructions = edtMealInstructions.text.toString()
                            val mealCategory = edtMealCategory.selectedItem as String

                            // Updates the selected custom meal then updates the whole list by calling
                            // the function refreshData from the fragment after dismissing the dialog
                            val coroutineContext = Job() + Dispatchers.IO
                            val scope = CoroutineScope(coroutineContext + CoroutineName("updateCustomMeal"))
                            scope.launch(Dispatchers.IO) {
                                if (username != null) {
                                    database.updateCustomMeal(username, editMealId, mealName, mealCategory, mealIngredients, mealInstructions)
                                }
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Custom meal has been updated.", Toast.LENGTH_SHORT).show()
                                    refreshDataCallback.refreshData()
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
        }
    }
}
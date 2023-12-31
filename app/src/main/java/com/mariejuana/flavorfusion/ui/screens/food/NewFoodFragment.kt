package com.mariejuana.flavorfusion.ui.screens.food

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mariejuana.flavorfusion.data.adapters.CustomMealAdapter
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.data.database.realm.models.CustomMealModel
import com.mariejuana.flavorfusion.data.models.meals.custom.CustomMeal
import com.mariejuana.flavorfusion.databinding.FragmentNewFoodBinding
import com.mariejuana.flavorfusion.ui.screens.food.dialogs.AddCustomMealDialog
import com.mariejuana.flavorfusion.ui.screens.food.dialogs.EditCustomMealDialog
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewFoodFragment : Fragment(),
    AddCustomMealDialog.RefreshDataInterface,
    EditCustomMealDialog.RefreshDataInterface,
    CustomMealAdapter.MealAdapterInterface {
    private lateinit var binding: FragmentNewFoodBinding
    private lateinit var mealList: ArrayList<CustomMeal>
    private lateinit var adapter: CustomMealAdapter

    private var database = RealmDatabase()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewFoodBinding.inflate(layoutInflater, container, false)

        // Set the array list for the custom meals
        mealList = arrayListOf()
        adapter = CustomMealAdapter(mealList, requireContext(), this, this)

        // Loads the recyclerview to be able to load them after creating the view
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        with(binding) {
            rvSearchMeal.layoutManager = layoutManager
            rvSearchMeal.adapter = adapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Uses the Shared Preferences in order to share the email into other fragments or activity
        val sharedPref = activity?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        // Shows the add custom meal dialog
        binding.fabAdd.setOnClickListener {
            val addMealDialog = AddCustomMealDialog()
            val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
            addMealDialog.refreshDataCallback = this
            addMealDialog.show(manager, null)
        }

        // Searches the Realm database if there's a matching name of the custom meal made by the current user
        // This shows either the result or the text saying "No result found"
        binding.idAllMealSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("SearchCustomMeal"))
                val searchFaveMeal = binding.idAllMealSearch.text.toString().lowercase()

                scope.launch(Dispatchers.IO) {
                    val result = username?.let { database.getCustomMealByName(it, searchFaveMeal) }

                    mealList = arrayListOf()
                    if (result != null) {
                        mealList.addAll(
                            result.map {
                                mapCustomMeal(it)
                            }
                        )
                    }

                    withContext(Dispatchers.Main) {
                        adapter.updateMeal(mealList)
                        if (mealList.isEmpty()) {
                            binding.txtNoSearchMatch.visibility = View.VISIBLE
                            if (searchFaveMeal == "") { getAllCustomMeal() }
                        } else {
                            binding.txtNoSearchMatch.visibility = View.GONE
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nothing to do
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Nothing to do
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getAllCustomMeal()
    }

    // This refreshes the data once there's an update made for the custom meal or added a new custom meal
    override fun refreshData() {
        getAllCustomMeal()
    }

    // Removes the custom meal made by the current user
    override fun removeCustomFood(username: String, meal: CustomMeal) {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("removeCustomMeal"))
        scope.launch(Dispatchers.IO) {
            database.deleteCustomMeal(username, meal)
            getAllCustomMeal()
        }
    }

    // Makes a "map" of the recycler view contents
    private fun mapCustomMeal(customMeal: CustomMealModel): CustomMeal {
        return CustomMeal(
            id = customMeal.id.toHexString(),
            name = customMeal.name,
            category = customMeal.category,
            instruction = customMeal.instruction,
            ingredient = customMeal.ingredient
        )
    }

    // Initializes the data from the Realm database and shows it to the screen
    // If the result is null or there's no custom meal made, it will show "No custom meal"
    private fun getAllCustomMeal() {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("loadAllCustomMeal"))
        val sharedPref = activity?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        scope.launch(Dispatchers.IO) {
            val customMeal = username?.let { database.getCustomMealsByUsername(it) }
            mealList = arrayListOf()
            if (customMeal != null) {
                mealList.addAll(
                    customMeal.map {
                        mapCustomMeal(it)
                    }
                )
            }

            withContext(Dispatchers.Main) {
                adapter.updateMeal(mealList)
                if (mealList.isEmpty()) {
                    binding.rvSearchMeal.visibility = View.GONE
                    binding.txtNoCustomMeal.visibility = View.VISIBLE
                    binding.txtNoSearchMatch.visibility = View.GONE
                } else {
                    binding.rvSearchMeal.visibility = View.VISIBLE
                    binding.txtNoCustomMeal.visibility = View.GONE
                    binding.txtNoSearchMatch.visibility = View.GONE
                }
            }
        }
    }
}
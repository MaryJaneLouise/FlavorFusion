package com.mariejuana.flavorfusion.ui.screens.food.selected

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.mariejuana.flavorfusion.data.helpers.queries.SearchMealQuery
import com.mariejuana.flavorfusion.data.helpers.retrofit.RetrofitHelper
import com.mariejuana.flavorfusion.data.models.meals.SearchMeal
import com.mariejuana.flavorfusion.databinding.FragmentSelectedCustomFoodBinding
import com.mariejuana.flavorfusion.databinding.FragmentSelectedFoodBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CustomFoodInformationFragment : DialogFragment() {
    private lateinit var binding: FragmentSelectedCustomFoodBinding
    private lateinit var mealData: ArrayList<SearchMeal>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectedCustomFoodBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    // Initializes the layout style of the dialog fragment
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instead of using Shared Preferences, the app uses Bundle Arguments as a way of sharing the data
        // from another fragment to this dialog
        val bundle = arguments
        val mealName = bundle!!.getString("ShowCustomFoodName")
        val mealCategory = bundle!!.getString("ShowCustomFoodCategory")
        val mealInstruction = bundle!!.getString("ShowCustomFoodInstructions")
        val mealIngredient = bundle!!.getString("ShowCustomFoodIngredients")

        // Converts the variable into a string
        val showMealName = mealName.toString()
        val showMealCategory = mealCategory.toString()
        val showMealInstruction = mealInstruction.toString()
        val showMealIngredient = mealIngredient.toString()

        // Shows the details after getting the values from the Realm database
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                binding.txtMealName.text =  showMealName
                binding.txtMealCategory.text = showMealCategory

                binding.ingredientsList.text = showMealIngredient
                binding.instructionList.text = showMealInstruction
            }
        }
    }
}
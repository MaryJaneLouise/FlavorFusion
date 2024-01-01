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
import com.mariejuana.flavorfusion.data.models.meals.Meal
import com.mariejuana.flavorfusion.data.models.meals.SearchMeal
import com.mariejuana.flavorfusion.databinding.FragmentSelectedFoodBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SelectedFoodInformationFragment : DialogFragment() {
    private lateinit var binding: FragmentSelectedFoodBinding
    private lateinit var mealData: ArrayList<Meal>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectedFoodBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val foodName = bundle!!.getString("FoodName")
        val searchMeal = foodName.toString()

        binding.mealSource.visibility = View.GONE
        binding.textView7.visibility = View.GONE

        mealData = arrayListOf()
        lifecycleScope.launch(Dispatchers.IO) {
            val searchMealInitiate = RetrofitHelper.getInstance().create(SearchMealQuery::class.java)
            val returnSearchMeal = searchMealInitiate.getSearchedMeal(searchMeal)
            val searchMealBody = returnSearchMeal.body()

            if (searchMealBody.toString() != "SearchMeals(meals=null)") {
                mealData.clear()
                mealData.addAll(searchMealBody!!.meals)

                val ingredientsMeasure = StringBuilder()
                val measuresIngredientsList = listOf(
                    Pair(mealData[0].strMeasure1, mealData[0].strIngredient1),
                    Pair(mealData[0].strMeasure2, mealData[0].strIngredient2),
                    Pair(mealData[0].strMeasure3, mealData[0].strIngredient3),
                    Pair(mealData[0].strMeasure4, mealData[0].strIngredient4),
                    Pair(mealData[0].strMeasure5, mealData[0].strIngredient5),
                    Pair(mealData[0].strMeasure6, mealData[0].strIngredient6),
                    Pair(mealData[0].strMeasure7, mealData[0].strIngredient7),
                    Pair(mealData[0].strMeasure8, mealData[0].strIngredient8),
                    Pair(mealData[0].strMeasure9, mealData[0].strIngredient9),
                    Pair(mealData[0].strMeasure10, mealData[0].strIngredient10),
                    Pair(mealData[0].strMeasure11, mealData[0].strIngredient11),
                    Pair(mealData[0].strMeasure12, mealData[0].strIngredient12),
                    Pair(mealData[0].strMeasure13, mealData[0].strIngredient13),
                    Pair(mealData[0].strMeasure14, mealData[0].strIngredient14),
                    Pair(mealData[0].strMeasure15, mealData[0].strIngredient15),
                    Pair(mealData[0].strMeasure16, mealData[0].strIngredient16),
                    Pair(mealData[0].strMeasure17, mealData[0].strIngredient17),
                    Pair(mealData[0].strMeasure18, mealData[0].strIngredient18),
                    Pair(mealData[0].strMeasure19, mealData[0].strIngredient19),
                    Pair(mealData[0].strMeasure20, mealData[0].strIngredient20)
                )

                for ((measure, ingredient) in measuresIngredientsList) {
                    if (!measure.isNullOrBlank() && !ingredient.isNullOrBlank()) {
                        ingredientsMeasure.append("$measure $ingredient\n")
                    }
                }

                withContext(Dispatchers.Main) {
                    binding.txtMealName.text =  mealData[0].strMeal
                    binding.txtMealCategory.text = mealData[0].strCategory
                    binding.txtMealArea.text = mealData[0].strArea

                    binding.ingredientsList.text = ingredientsMeasure
                    binding.instructionList.text = mealData[0].strInstructions

                    if (!mealData[0].strSource.isNullOrBlank()) {
                        binding.mealSource.visibility = View.VISIBLE
                        binding.textView7.visibility = View.VISIBLE
                        binding.mealSource.text = mealData[0].strSource
                    } else {
                        binding.textView7.visibility = View.GONE
                        binding.mealSource.visibility = View.GONE
                    }

                    Glide.with(requireContext())
                        .load(mealData[0].strMealThumb)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(200,200)
                        .transform(CenterCrop(), RoundedCorners(25))
                        .into(binding.mealImage)
                }
            }
        }
    }

}
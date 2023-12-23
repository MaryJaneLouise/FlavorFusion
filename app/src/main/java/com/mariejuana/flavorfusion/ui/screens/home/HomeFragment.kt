package com.mariejuana.flavorfusion.ui.screens.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.data.adapters.MealAdapter
import com.mariejuana.flavorfusion.data.helpers.realm.RealmDatabase
import com.mariejuana.flavorfusion.data.helpers.repositories.HomeMealQuery
import com.mariejuana.flavorfusion.data.helpers.repositories.RandomMealQuery
import com.mariejuana.flavorfusion.data.helpers.retrofit.RetrofitHelper
import com.mariejuana.flavorfusion.data.models.meals.Meal
import com.mariejuana.flavorfusion.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), MealAdapter.MealAdapterInterface {
    private lateinit var _binding: FragmentHomeBinding
    private lateinit var adapter: MealAdapter
    private lateinit var mealData: ArrayList<Meal>
    private lateinit var meal: Meal
    private lateinit var auth : FirebaseAuth

    private var database = RealmDatabase()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Set the array list for the meals
        mealData = arrayListOf()
        adapter = MealAdapter(mealData, requireContext(), this)

        // Loads the recyclerview to be able to load them after creating the view
        // It loads horizontally, otherwise it will be no good when loaded vertically
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        with(binding) {
            rvMeals.layoutManager = layoutManager
            rvMeals.adapter = adapter
        }

        // Loads the necessary data for the randomized food
        val randomMealInitiate = RetrofitHelper.getInstance().create(RandomMealQuery::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            val returnMeal = randomMealInitiate.getRandomMeal()
            val randomMealBody = returnMeal.body()

            if (randomMealBody != null) {
                with(binding) {
                    txtMealName.text = randomMealBody.meals.map { it.strMeal }.joinToString(", ")
                    txtMealCategory.text = randomMealBody.meals.map { it.strCategory }.joinToString(", ")
                    txtMealArea.text = randomMealBody.meals.map { it.strArea }.joinToString(", ")
                }

                withContext(Dispatchers.Main) {
                    Glide.with(requireContext())
                        .load(randomMealBody.meals.map { it.strMealThumb }.joinToString(", "))
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(200,200)
                        .into(binding.mealImage)
                }
            }
        }

        // Loads the necessary data for the recyclerview
        val mealInitiate = RetrofitHelper.getInstance().create(HomeMealQuery::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            val resultMeal = mealInitiate.getSpecificMealsFromArea("Filipino")
            val mealBody = resultMeal.body()

            if (mealBody != null) {
                meal = mealBody.meals[0]
                mealData.clear()
                mealData.addAll(mealBody.meals)
                withContext(Dispatchers.Main) {
                    adapter.updateMeal(mealData)
                }
            }
        }

        return root
    }

    override fun addFaveFood(username: String, meal: Meal) {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("addFave"))
        scope.launch(Dispatchers.IO) {
//            database.addToFavorite(username, meal)
        }
    }
}

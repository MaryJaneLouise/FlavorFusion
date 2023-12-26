package com.mariejuana.flavorfusion.ui.screens.favorites

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.data.adapters.FaveMealAdapter
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.data.database.realm.models.MealModel
import com.mariejuana.flavorfusion.data.models.meals.Meal
import com.mariejuana.flavorfusion.databinding.FragmentFavoritesBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment(), FaveMealAdapter.MealAdapterInterface {
    private lateinit var _binding: FragmentFavoritesBinding
    private lateinit var adapter: FaveMealAdapter
    private lateinit var faveMealData: ArrayList<Meal>

    private var database = RealmDatabase()
    private lateinit var auth : FirebaseAuth
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set the array list for the ingredients
        faveMealData = arrayListOf()
        adapter = FaveMealAdapter(faveMealData, requireContext(), this)

        // Loads the recyclerview to be able to load them after creating the view
        // It loads horizontally, otherwise it will be no good when loaded vertically
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        with(binding) {
            rvMeals.layoutManager = layoutManager
            rvMeals.adapter = adapter
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        getAllFaveFood()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        binding.idFaveMealSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("SearchFaveMeal"))
                val searchFaveMeal = binding.idFaveMealSearch.text.toString().lowercase()

                scope.launch(Dispatchers.IO) {
                    val result = username?.let { database.getFavoriteMealsByName(it, searchFaveMeal) }
                    faveMealData = arrayListOf()
                    if (result != null) {
                        faveMealData.addAll(
                            result.map {
                                mapMeal(it)
                            }
                        )
                    }

                    withContext(Dispatchers.Main) {
                        adapter.updateMeal(faveMealData)
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

    override fun removeFaveFood(username: String, meal: Meal) {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("removeFave"))
        scope.launch(Dispatchers.IO) {
            database.removeFromFavorite(username, meal)
        }
    }

    private fun mapMeal(meal: MealModel): Meal {
        return Meal(
            idMeal = meal.idMeal,
            strMeal = meal.name,
            strArea = meal.area,
            strCategory = meal.category,
            strDrinkAlternate = meal.drinkAlternate,
            strInstructions = meal.instructions,
            strMealThumb = meal.mealThumb,
            strTags = meal.tags,
        )
    }

    private fun getAllFaveFood() {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("loadAllFaveFood"))
        val sharedPref = activity?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        scope.launch(Dispatchers.IO) {
            val meals = username?.let { database.getFavoriteMealsByUsername(it) }
            faveMealData = arrayListOf()
            if (meals != null) {
                faveMealData.addAll(
                    meals.map {
                        mapMeal(it)
                    }
                )
            }

            withContext(Dispatchers.Main) {
                adapter.updateMeal(faveMealData)
                if (faveMealData.isEmpty()) {
                    binding.rvMeals.visibility = View.GONE
                    binding.txtNoFaveAvailable.visibility = View.VISIBLE
                } else {
                    binding.rvMeals.visibility = View.VISIBLE
                    binding.txtNoFaveAvailable.visibility = View.GONE
                }
            }
        }
    }
}
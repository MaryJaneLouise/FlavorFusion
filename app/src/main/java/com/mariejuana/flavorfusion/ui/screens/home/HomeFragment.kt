package com.mariejuana.flavorfusion.ui.screens.home

import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.data.adapters.MealAdapter
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.data.helpers.queries.AreaAllQuery
import com.mariejuana.flavorfusion.data.helpers.queries.HomeMealQuery
import com.mariejuana.flavorfusion.data.helpers.queries.RandomMealQuery
import com.mariejuana.flavorfusion.data.helpers.retrofit.RetrofitHelper
import com.mariejuana.flavorfusion.data.models.meals.Area
import com.mariejuana.flavorfusion.data.models.meals.Meal
import com.mariejuana.flavorfusion.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment(), MealAdapter.MealAdapterInterface {
    private lateinit var _binding: FragmentHomeBinding
    private lateinit var adapter: MealAdapter
    private lateinit var mealData: ArrayList<Meal>
    private lateinit var areaData: ArrayList<Area>
    private lateinit var meal: Meal
    private lateinit var area: Area
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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val username = currentUser?.email

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

        class RandomMealWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
            override suspend fun doWork(): Result {
                // Call the function when the app starts
                lifecycleScope.launch(Dispatchers.IO) {
                    fetchAndDisplayRandomMeal()

                    withContext(Dispatchers.Main) {
                        val randomMeal = username?.let { getRandomMeal(it) }
                        if (randomMeal != null) {
                            with(binding) {
                                txtMealName.text = database.getRandomMeal(username)?.name
                                txtMealArea.text = database.getRandomMeal(username)?.area
                            }
                        }
                    }
                }
                return Result.success()
            }
        }

        val randomMealWorkRequest = PeriodicWorkRequestBuilder<RandomMealWorker>(24, TimeUnit.HOURS).build()
        context?.let { WorkManager.getInstance(it).enqueue(randomMealWorkRequest) }

        // Call the function when the app starts
        lifecycleScope.launch(Dispatchers.IO) {
            fetchAndDisplayRandomMeal()
        }

        // Call the function when the button is clicked
        binding.buttonRandomFood.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                fetchAndDisplayRandomMeal()
            }
        }

        // Set the array list for the areas
        val areaNamesInitiate = RetrofitHelper.getInstance().create(AreaAllQuery::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            val resultArea = areaNamesInitiate.getAllAreas()
            val areaNames = resultArea.body()

            if (areaNames != null) {
                withContext(Dispatchers.Main) {
                    val areaListSpinner = areaNames.meals.map { it.strArea }
                    val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, areaListSpinner)
                    with(binding) {
                        areaMeal.adapter = spinnerAdapter
                        areaMeal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                val selectedArea = parent.getItemAtPosition(position).toString()
                                getLocationSpecialty(selectedArea)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // Nothing to do.. literally
                            }
                        }
                    }
                }
            }
        }
    }

    // Fetch and display the random meal
    suspend fun fetchAndDisplayRandomMeal() {
        val sharedPref = context?.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        val randomMealInitiate = RetrofitHelper.getInstance().create(RandomMealQuery::class.java)
        val returnMeal = randomMealInitiate.getRandomMeal()
        val randomMealBody = returnMeal.body()

        if (randomMealBody != null) {
            withContext(Dispatchers.Main) {
                with(binding) {
                    txtMealName.text = randomMealBody.meals.joinToString(", ") { it.strMeal }
                    txtMealCategory.text = randomMealBody.meals.joinToString(", ") { it.strCategory }
                    txtMealArea.text = randomMealBody.meals.joinToString(", ") { it.strArea }
                }

                Glide.with(requireContext())
                    .load(randomMealBody.meals.map { it.strMealThumb }.joinToString(", "))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .override(200,200)
                    .into(binding.mealImage)

//                lifecycleScope.launch(Dispatchers.IO) {
//                    if (username != null) {
//                        addRandomFood(username, randomMealBody.meals[0])
//                    }
//                }

//                binding.buttonRandomFood.setOnClickListener {
//                    lifecycleScope.launch(Dispatchers.IO) {
//                        if (username != null) {
//                            addRandomFood(username, randomMealBody.meals[0])
//                        }
//                    }
//                }

                binding.buttonFaveFood.setOnClickListener {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("Are you sure you want to add this food to the favorites?")
                    builder.setTitle("Warning!")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        if (username != null) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                addFaveFood(username, randomMealBody.meals[0])
                            }
                        }
                        Toast.makeText(context, "The selected food has been placed in favorites.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
        }
    }

    private fun getLocationSpecialty(area: String) {
        // Loads the necessary data for the recyclerview
        val mealInitiate = RetrofitHelper.getInstance().create(HomeMealQuery::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            val resultMeal = mealInitiate.getSpecificMealsFromArea(area)
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
    }


    // Save the food to the favorites
    override fun addFaveFood(username: String, meal: Meal) {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("addFave"))
        scope.launch(Dispatchers.IO) {
            database.addToFavorite(username, meal)
        }
    }

    // Saves the random meal to the database of the current user
    private fun addRandomFood(username: String, meal: Meal) {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("addRandom"))
        scope.launch(Dispatchers.IO) {
            database.addRandomMeal(username, meal)
        }
    }

    // Gets the random meal for the current user
    private fun getRandomMeal(username: String) {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("getRandom"))
        scope.launch(Dispatchers.IO) {
            database.getRandomMeal(username)
        }
    }
}

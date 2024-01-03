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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.data.adapters.MealAdapter
import com.mariejuana.flavorfusion.data.constants.StateAPI
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.data.helpers.queries.AreaAllQuery
import com.mariejuana.flavorfusion.data.helpers.queries.HomeMealQuery
import com.mariejuana.flavorfusion.data.helpers.queries.RandomMealQuery
import com.mariejuana.flavorfusion.data.helpers.queries.api.RandomMealRepository
import com.mariejuana.flavorfusion.data.helpers.retrofit.RetrofitHelper
import com.mariejuana.flavorfusion.data.models.meals.Area
import com.mariejuana.flavorfusion.data.models.meals.Meal
import com.mariejuana.flavorfusion.databinding.FragmentHomeBinding
import com.mariejuana.flavorfusion.ui.screens.food.selected.SelectedFoodInformationFragment
import com.mariejuana.flavorfusion.ui.screens.home.viewmodel.HomeFragmentFactory
import com.mariejuana.flavorfusion.ui.screens.home.viewmodel.HomeFragmentViewModel
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
    private lateinit var viewModel: HomeFragmentViewModel

    private var database = RealmDatabase()
    private var buttonsVisible = false

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

        // Initializes the value for the StateAPI either success, etc. that is coming
        // from the view model and its factory
        viewModel = ViewModelProvider(this, HomeFragmentFactory(RandomMealRepository()))[HomeFragmentViewModel::class.java]
        binding.mealFullDashboard.visibility = View.GONE
        viewModel.mealRandomData()

        // Initializes the credentials of the current user
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

        // Initializes the value for the hidden / shown buttons
        buttonsVisible = false
        binding.buttonAddRandom.visibility = View.GONE

        // If the card has been pressed, it will show the buttons
        binding.cvMeal.setOnClickListener {
            if (!buttonsVisible) {
                binding.buttonAddRandom.visibility = View.VISIBLE
                buttonsVisible = true
            } else {
                binding.buttonAddRandom.visibility = View.GONE
                buttonsVisible = false
            }
        }

        // Call the function when the button is clicked and loads the lottie animation
        binding.buttonRandomFood.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.mealStateVM.collect {
                    when (it) {
                        is StateAPI.Loading -> {
                            withContext(Dispatchers.Main) {
                                binding.animationView.visibility = View.VISIBLE
                            }
                        }
                        is StateAPI.Success -> {
                            lifecycleScope.launch(Dispatchers.IO) {
                                fetchAndDisplayRandomMeal()
                                withContext(Dispatchers.Main) {
                                    binding.animationView.visibility = View.GONE
                                }
                            }
                        }
                        is StateAPI.Failure -> {
                            it.e.printStackTrace()
                        }
                        is StateAPI.Empty -> {
                            withContext(Dispatchers.Main) {
                                binding.animationView.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }

        // Loads the lottie animation first before loading the data
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.mealStateVM.collect {
                when (it) {
                    is StateAPI.Loading -> {
                        withContext(Dispatchers.Main) {
                            binding.animationView.visibility = View.VISIBLE
                        }
                    }
                    is StateAPI.Success -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            // Runs the function that loads the data to the screen
                            fetchAndDisplayRandomMeal()

                            // Hides the lottie animation and shows the necessary screens of the Home Fragment
                            withContext(Dispatchers.Main) {
                                binding.mealFullDashboard.visibility = View.VISIBLE
                                binding.animationView.visibility = View.GONE
                            }

                            // Set the array list for the areas
                            val areaNamesInitiate = RetrofitHelper.getInstance().create(AreaAllQuery::class.java)
                            lifecycleScope.launch(Dispatchers.IO) {
                                val resultArea = areaNamesInitiate.getAllAreas()
                                val areaNames = resultArea.body()

                                // Sets the values got and places them in the spinner
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
                    }
                    is StateAPI.Failure -> {
                        it.e.printStackTrace()
                    }
                    is StateAPI.Empty -> {
                        withContext(Dispatchers.Main) {
                            binding.animationView.visibility = View.VISIBLE
                            binding.mealFullDashboard.visibility = View.GONE
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
                    .transform(CenterCrop(), RoundedCorners(25))
                    .into(binding.mealImage)

                binding.buttonShowDetails.setOnClickListener {
                    var a  = SelectedFoodInformationFragment()
                    val manager: FragmentManager =
                        (context as AppCompatActivity).supportFragmentManager
                    val bundle = Bundle()
                    bundle.putString("FoodName", randomMealBody.meals.joinToString(", ") { it.strMeal });
                    a.arguments = bundle
                    a.show(manager,"")
                }

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

    // Loads the necessary data for the recyclerview
    private fun getLocationSpecialty(area: String) {
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


    // Save the food to the favorites of the current user
    override fun addFaveFood(username: String, meal: Meal) {
        val coroutineContext = Job() + Dispatchers.IO
        val scope = CoroutineScope(coroutineContext + CoroutineName("addFave"))
        scope.launch(Dispatchers.IO) {
            database.addToFavorite(username, meal)
        }
    }
}

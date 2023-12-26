package com.mariejuana.flavorfusion.ui.screens.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.data.adapters.MealAdapter
import com.mariejuana.flavorfusion.data.adapters.SearchMealAdapter
import com.mariejuana.flavorfusion.data.database.realm.RealmDatabase
import com.mariejuana.flavorfusion.data.helpers.queries.SearchMealQuery
import com.mariejuana.flavorfusion.data.helpers.retrofit.RetrofitHelper
import com.mariejuana.flavorfusion.data.models.meals.Meal
import com.mariejuana.flavorfusion.data.models.meals.SearchMeal
import com.mariejuana.flavorfusion.databinding.FragmentSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.create

class SearchFragment : Fragment(), SearchMealAdapter.MealAdapterInterface {
    private lateinit var _binding: FragmentSearchBinding
    private lateinit var adapter: SearchMealAdapter
    private lateinit var mealData: ArrayList<SearchMeal>
    private lateinit var mealSearch: SearchMeal
    private lateinit var auth: FirebaseAuth

    private var database = RealmDatabase()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
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
        adapter = SearchMealAdapter(mealData, requireContext(), this)

        // Loads the recyclerview to be able to load them after creating the view
        // It loads horizontally, otherwise it will be no good when loaded vertically
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        with(binding) {
            rvSearchMeal.layoutManager = layoutManager
            rvSearchMeal.adapter = adapter
        }

        binding.txtNoSearchMatch.visibility = View.GONE
        binding.idAllMealSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val searchMeal = binding.idAllMealSearch.text.toString().lowercase()

                lifecycleScope.launch(Dispatchers.IO) {
                    val searchMealInitiate = RetrofitHelper.getInstance().create(SearchMealQuery::class.java)
                    val returnSearchMeal = searchMealInitiate.getSearchedMeal(searchMeal)
                    val searchMealBody = returnSearchMeal.body()

                    if (searchMealBody != null) {
                        mealSearch = searchMealBody.meals[0]
                        mealData.clear()
                        mealData.addAll(searchMealBody.meals)
                        withContext(Dispatchers.Main) {
                            adapter.updateMeal(mealData)
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

    override fun addFaveFood(username: String, meal: SearchMeal) {
        // Nothing to do for now
    }
}
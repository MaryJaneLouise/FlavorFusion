package com.mariejuana.flavorfusion.ui.screens.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mariejuana.flavorfusion.data.adapters.IngredientAdapter
import com.mariejuana.flavorfusion.data.helpers.repositories.AllIngredientQuery
import com.mariejuana.flavorfusion.data.helpers.retrofit.RetrofitHelper
import com.mariejuana.flavorfusion.data.models.meals.Ingredient
import com.mariejuana.flavorfusion.databinding.FragmentFavoritesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment() {
    private lateinit var _binding: FragmentFavoritesBinding
    private lateinit var adapter: IngredientAdapter
    private lateinit var ingredientData: ArrayList<Ingredient>
    private lateinit var ingredient: Ingredient

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set the array list for the ingredients
        ingredientData = arrayListOf()
        adapter = IngredientAdapter(ingredientData, requireContext())

        // Loads the recyclerview to be able to load them after creating the view
        // It loads horizontally, otherwise it will be no good when loaded vertically
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        with(binding) {
            rvMeals.layoutManager = layoutManager
            rvMeals.adapter = adapter
        }

        // Loads the necessary data for the recyclerview
        lifecycleScope.launch(Dispatchers.IO) {

        }

        return root
    }
}
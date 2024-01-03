package com.mariejuana.flavorfusion.ui.screens.food.selected.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mariejuana.flavorfusion.data.helpers.queries.api.SearchMealRepository
import com.mariejuana.flavorfusion.ui.screens.home.viewmodel.HomeFragmentViewModel

class SelectedFoodInformationFragmentFactory(private val loadSearchedMeal: SearchMealRepository):  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SelectedFoodInformationFragmentViewModel(loadSearchedMeal) as T
    }
}
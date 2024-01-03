package com.mariejuana.flavorfusion.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mariejuana.flavorfusion.data.helpers.queries.api.RandomMealRepository

class HomeFragmentFactory(private val loadRandomMeal: RandomMealRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeFragmentViewModel(loadRandomMeal) as T
    }
}
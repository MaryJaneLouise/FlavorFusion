package com.mariejuana.flavorfusion.ui.screens.food.selected.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariejuana.flavorfusion.data.constants.StateAPI
import com.mariejuana.flavorfusion.data.helpers.queries.api.SearchMealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SelectedFoodInformationFragmentViewModel(private val loadSearchMeal: SearchMealRepository): ViewModel() {
    val mealStateVM: MutableStateFlow<StateAPI> = MutableStateFlow(StateAPI.Empty)

    fun getSearchedMeal(mealName: String) = viewModelScope.launch(Dispatchers.IO) {
        mealStateVM.value = StateAPI.Loading
        loadSearchMeal.getSearchMeal(mealName)
            .catch {
                mealStateVM.value = StateAPI.Failure(it)
            }.collect {
                mealStateVM.value = StateAPI.Success(it)
            }
    }
}
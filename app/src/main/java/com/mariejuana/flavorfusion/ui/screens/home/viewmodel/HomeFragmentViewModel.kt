package com.mariejuana.flavorfusion.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariejuana.flavorfusion.data.constants.StateAPI
import com.mariejuana.flavorfusion.data.helpers.queries.RandomMealQuery
import com.mariejuana.flavorfusion.data.helpers.queries.api.RandomMealRepository
import io.reactivex.internal.operators.single.SingleDoAfterTerminate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeFragmentViewModel(private val loadRandomMeal: RandomMealRepository): ViewModel() {
    val mealStateVM: MutableStateFlow<StateAPI> = MutableStateFlow(StateAPI.Empty)

    fun mealRandomData() = viewModelScope.launch(Dispatchers.IO) {
        mealStateVM.value = StateAPI.Loading
        loadRandomMeal.getRandomMeal()
            .catch {
                mealStateVM.value = StateAPI.Failure(it)
            }.collect {
                mealStateVM.value = StateAPI.Success(it)
            }
    }
}
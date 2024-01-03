package com.mariejuana.flavorfusion.data.helpers.queries.api

import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.helpers.queries.RandomMealQuery
import com.mariejuana.flavorfusion.data.helpers.queries.SearchMealQuery
import com.mariejuana.flavorfusion.data.helpers.retrofit.RetrofitHelper
import com.mariejuana.flavorfusion.data.models.meals.SearchMeal
import com.mariejuana.flavorfusion.data.models.meals.query.Meals
import com.mariejuana.flavorfusion.data.models.meals.query.SearchMeals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import retrofit2.create

class SearchMealRepository {
    fun getSearchMeal(mealName: String): Flow<Response<SearchMeals>> = flow {
        val retrofit = RetrofitHelper.getInstance().create(SearchMealQuery::class.java).getSearchedMeal(mealName)
        emit(retrofit)
    }.flowOn(Dispatchers.IO)
}
package com.mariejuana.flavorfusion.data.helpers.queries.api

import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.helpers.queries.RandomMealQuery
import com.mariejuana.flavorfusion.data.helpers.retrofit.RetrofitHelper
import com.mariejuana.flavorfusion.data.models.meals.query.Meals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import retrofit2.create

class RandomMealRepository {
    fun getRandomMeal(): Flow<Response<Meals>> = flow {
        val retrofit = RetrofitHelper.getInstance().create(RandomMealQuery::class.java).getRandomMeal()
        emit(retrofit)
    }.flowOn(Dispatchers.IO)
}
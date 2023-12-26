package com.mariejuana.flavorfusion.data.helpers.queries

import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.models.meals.query.Meals
import retrofit2.Response
import retrofit2.http.GET

interface RandomMealQuery {
    @GET(API.MEAL_GENERATE_RANDOM)
    suspend fun getRandomMeal(): Response<Meals>
}
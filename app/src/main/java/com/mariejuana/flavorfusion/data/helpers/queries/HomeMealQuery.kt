package com.mariejuana.flavorfusion.data.helpers.queries

import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.models.meals.query.Meals
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeMealQuery {
    @GET(API.MEAL_FILTER_AREA)
    suspend fun getSpecificMealsFromArea(
        @Query("a") location: String
    ): Response<Meals>
}
package com.mariejuana.flavorfusion.data.helpers.queries

import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.models.meals.query.SearchMeals
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchMealQuery {
    @GET(API.MEAL_SEARCH_FOOD)
    suspend fun getSearchedMeal(
        @Query("s") mealName: String
    ): Response<SearchMeals>
}
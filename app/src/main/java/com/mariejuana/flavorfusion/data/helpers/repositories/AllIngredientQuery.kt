package com.mariejuana.flavorfusion.data.helpers.repositories

import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.models.meals.query.Ingredients
import retrofit2.Response
import retrofit2.http.GET

interface AllIngredientQuery {
    @GET(API.INGREDIENT_LIST)
    suspend fun getAllIngredients(): Response<Ingredients>
}
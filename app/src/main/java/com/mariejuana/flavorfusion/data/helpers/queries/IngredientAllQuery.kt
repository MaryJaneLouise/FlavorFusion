package com.mariejuana.flavorfusion.data.helpers.queries

import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.models.meals.query.Ingredients
import retrofit2.Response
import retrofit2.http.GET

interface IngredientAllQuery {
    @GET(API.INGREDIENT_LIST)
    suspend fun getAllIngredients(): Response<Ingredients>
}
package com.mariejuana.flavorfusion.data.helpers.queries

import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.models.meals.query.Categories
import retrofit2.Response
import retrofit2.http.GET

interface CategoryAllQuery {
    @GET(API.CATEGORY_LIST)
    suspend fun getAllCategories(): Response<Categories>
}
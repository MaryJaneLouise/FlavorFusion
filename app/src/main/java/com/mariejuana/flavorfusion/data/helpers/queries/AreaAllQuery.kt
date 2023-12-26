package com.mariejuana.flavorfusion.data.helpers.queries

import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.models.meals.query.Areas
import retrofit2.Response
import retrofit2.http.GET

interface AreaAllQuery {
    @GET(API.AREA_LIST)
    suspend fun getAllAreas(): Response<Areas>
}
package com.mariejuana.flavorfusion.data.helpers.retrofit

import com.mariejuana.flavorfusion.data.constants.API.MEAL_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(MEAL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
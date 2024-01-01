package com.mariejuana.flavorfusion.data.models.meals.query

import com.mariejuana.flavorfusion.data.models.meals.Meal
import java.io.Serializable

data class SearchMeals(
    val meals: ArrayList<Meal>
): Serializable
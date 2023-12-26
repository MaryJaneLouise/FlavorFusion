package com.mariejuana.flavorfusion.data.models.meals.query

import com.mariejuana.flavorfusion.data.models.meals.SearchMeal
import java.io.Serializable

data class SearchMeals(
    val meals: ArrayList<SearchMeal>
): Serializable
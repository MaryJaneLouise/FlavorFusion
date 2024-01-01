package com.mariejuana.flavorfusion.data.models.meals.query

import com.mariejuana.flavorfusion.data.models.meals.MealCategory
import java.io.Serializable

data class Categories (
    val meals: ArrayList<MealCategory>
): Serializable
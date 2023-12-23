package com.mariejuana.flavorfusion.data.models.meals.query

import com.mariejuana.flavorfusion.data.models.meals.Meal
import java.io.Serializable

data class Meals (
    val meals: ArrayList<Meal>
): Serializable
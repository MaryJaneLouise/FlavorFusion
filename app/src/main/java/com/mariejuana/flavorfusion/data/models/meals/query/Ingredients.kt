package com.mariejuana.flavorfusion.data.models.meals.query

import com.mariejuana.flavorfusion.data.models.meals.Ingredient
import java.io.Serializable

data class Ingredients(
    val meals: ArrayList<Ingredient>
): Serializable
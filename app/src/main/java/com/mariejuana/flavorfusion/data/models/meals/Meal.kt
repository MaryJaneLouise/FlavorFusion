package com.mariejuana.flavorfusion.data.models.meals

import java.io.Serializable

data class Meal(
    var idMeal: String,
    var strMeal: String,
    var strMealThumb: String,
    var strDrinkAlternate: String?,
    var strCategory: String,
    var strArea: String,
    var strInstructions: String,
    var strTags: String,
): Serializable
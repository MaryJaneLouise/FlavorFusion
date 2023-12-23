package com.mariejuana.flavorfusion.data.models.meals

import java.io.Serializable

data class MealCategory(
    var idCategory: String,
    var strCategory: String,
    var strCategoryDescription: String,
): Serializable
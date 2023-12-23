package com.mariejuana.flavorfusion.data.models.meals

import java.io.Serializable

data class Ingredient(
    var idIngredient: String,
    var strIngredient: String,
    var strDescription: String,
    var strType: String
): Serializable
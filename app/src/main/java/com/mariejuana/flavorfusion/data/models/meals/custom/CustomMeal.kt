package com.mariejuana.flavorfusion.data.models.meals.custom

import org.mongodb.kbson.BsonObjectId

data class CustomMeal (
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val instruction: String = "",
    val ingredient: String = "",
)
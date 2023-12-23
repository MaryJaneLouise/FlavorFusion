package com.mariejuana.flavorfusion.data.models.users

data class User(
    val id: String,
    val name: String,
    val username: String,
    val password: String,
    val favoriteFoodCount: Int
)
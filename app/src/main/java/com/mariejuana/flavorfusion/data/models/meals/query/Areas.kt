package com.mariejuana.flavorfusion.data.models.meals.query

import com.mariejuana.flavorfusion.data.models.meals.Area
import java.io.Serializable

data class Areas(
    val meals: ArrayList<Area>
): Serializable
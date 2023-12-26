package com.mariejuana.flavorfusion.data.database.realm.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class MealModel: RealmObject {
    @PrimaryKey
    var idMeal: String = ""
    var name: String = ""
    var mealThumb: String = ""
    var drinkAlternate: String = ""
    var category: String = ""
    var area: String = ""
    var instructions: String = ""
    var tags: String = ""
}
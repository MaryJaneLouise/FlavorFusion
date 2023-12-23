package com.mariejuana.flavorfusion.data.helpers.realm.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class MealModel: RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var idMeal: String = ""
    var name: String = ""
}
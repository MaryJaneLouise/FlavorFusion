package com.mariejuana.flavorfusion.data.database.realm.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class UserModel: RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var name: String = ""
    var username: String = ""
    var password: String = ""
    var listFaveFood: RealmList<MealModel> = realmListOf()
    var listCustomFood: RealmList<CustomMealModel> = realmListOf()
    var randomFood: RandomMealModel? = null
}
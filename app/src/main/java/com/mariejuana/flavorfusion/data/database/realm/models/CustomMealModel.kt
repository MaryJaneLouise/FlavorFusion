package com.mariejuana.flavorfusion.data.database.realm.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class CustomMealModel: RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var name:String = ""
    var category: String = ""
    var instruction: String = ""
    var ingredient: String = ""
    var source: String = ""
}
package com.mariejuana.flavorfusion.data.helpers.realm

import com.mariejuana.flavorfusion.data.helpers.realm.models.MealModel
import com.mariejuana.flavorfusion.data.helpers.realm.models.UserModel
import com.mariejuana.flavorfusion.data.models.meals.Meal
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.lang.IllegalStateException

class RealmDatabase {
    // Set up the database
    private val realm : Realm by lazy {
        val config = RealmConfiguration
            .Builder(schema =  setOf(UserModel::class, MealModel::class))
            .schemaVersion(1)
            .build()
        Realm.open(config)
    }

    // Get all of the users list
    fun getAllUsers(): List<UserModel> {
        return realm.query<UserModel>().find()
    }

    // Get current user logged in
    fun getCurrentUserName(username: String): String? {
        val user = realm.query<UserModel>("username == $0", username).first().find()
        return user?.name
    }

    // Adds the user in the database
    suspend fun addUser(name: String, username: String, password: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult == null) {
                    val user = UserModel().apply {
                        this.name = name
                        this.username = username
                        this.password = password
                    }

                    copyToRealm(user)
                }
            }
        }
    }

    // Add a meal to the favorites of current user
    suspend fun addToFavorite(username: String, meal: MealModel) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult != null) {
                    findLatest(userResult)?.listFaveFood?.add(meal)
                }
            }
        }
    }

    // Delete meal from the favorites of the current user
    suspend fun removeFromFavorite(username: String, meal: MealModel) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult != null) {
                    findLatest(userResult)?.listFaveFood?.remove(meal)
                }
            }
        }
    }
}
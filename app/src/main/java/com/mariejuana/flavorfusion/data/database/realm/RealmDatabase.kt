package com.mariejuana.flavorfusion.data.database.realm

import com.mariejuana.flavorfusion.data.database.realm.models.MealModel
import com.mariejuana.flavorfusion.data.database.realm.models.RandomMealModel
import com.mariejuana.flavorfusion.data.database.realm.models.UserModel
import com.mariejuana.flavorfusion.data.models.meals.Meal
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.delete
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
            .Builder(schema =  setOf(UserModel::class, MealModel::class, RandomMealModel::class))
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

    // Gets the favorites of the current user
    fun getFavoriteMeals(username: String): List<MealModel>? {
        val user = realm.query<UserModel>("username == $0", username).first().find()
        return user?.listFaveFood
    }

    // Gets the randomized food data from the current user
    fun getRandomMeal(username: String): RandomMealModel? {
        val user = realm.query<UserModel>("username == $0", username).first().find()
        return user?.randomFood
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
    suspend fun addToFavorite(username: String, meal: Meal) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult != null) {
                    val mealResult: MealModel? = realm.query<MealModel>("idMeal == $0", meal.idMeal).first().find()

                    if (mealResult == null) {
                        val meal = MealModel().apply {
                            this.idMeal = meal.idMeal
                            this.name = meal.strMeal
                            this.drinkAlternate = meal.strDrinkAlternate ?: ""
                            this.mealThumb = meal.strMealThumb ?: ""
                            this.category = meal.strCategory ?: ""
                            this.area = meal.strArea ?: ""
                            this.instructions = meal.strInstructions ?: ""
                            this.tags = meal.strTags ?: ""
                        }

                        val saveMeal = copyToRealm(meal)
                        if (!findLatest(userResult)?.listFaveFood?.contains(saveMeal)!!) {
                            findLatest(userResult)?.listFaveFood?.add(saveMeal)
                        }
                    } else {
                        val mealExisting = findLatest(mealResult)
                        if (!findLatest(userResult)?.listFaveFood?.contains(mealExisting)!!) {
                            findLatest(userResult)?.listFaveFood?.add(mealExisting!!)
                        }
                    }
                }
            }
        }
    }

    // Delete meal from the favorites of the current user
    suspend fun removeFromFavorite(username: String, meal: Meal) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult != null) {
                    val mealResult: MealModel? = realm.query<MealModel>("idMeal == $0", meal.idMeal).first().find()

                    if (mealResult != null) {
                    val mealExisting = findLatest(mealResult)
                    findLatest(userResult)?.listFaveFood?.remove(mealExisting!!)
                    }
                }
            }
        }
    }

    // Saves the data about the queried data
    suspend fun addRandomMeal(username: String, meal: Meal) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult != null) {
                    userResult.randomFood?.let { oldMeal -> delete(oldMeal) }

                    val meal = RandomMealModel().apply {
                        this.idMeal = meal.idMeal
                        this.name = meal.strMeal
                        this.drinkAlternate = meal.strDrinkAlternate ?: ""
                        this.mealThumb = meal.strMealThumb ?: ""
                        this.category = meal.strCategory ?: ""
                        this.area = meal.strArea ?: ""
                        this.instructions = meal.strInstructions ?: ""
                        this.tags = meal.strTags ?: ""
                    }

                    val saveMeal = copyToRealm(meal)
                    userResult.randomFood = saveMeal
                }
            }
        }
    }
}
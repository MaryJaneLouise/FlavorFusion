package com.mariejuana.flavorfusion.data.database.realm

import com.mariejuana.flavorfusion.data.database.realm.models.MealModel
import com.mariejuana.flavorfusion.data.database.realm.models.CustomMealModel
import com.mariejuana.flavorfusion.data.database.realm.models.RandomMealModel
import com.mariejuana.flavorfusion.data.database.realm.models.UserModel
import com.mariejuana.flavorfusion.data.models.meals.Ingredient
import com.mariejuana.flavorfusion.data.models.meals.Meal
import com.mariejuana.flavorfusion.data.models.meals.custom.CustomMeal
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.lang.IllegalStateException

class RealmDatabase {
    // Set up the database
    private val realm : Realm by lazy {
        val config = RealmConfiguration
            .Builder(schema =  setOf(UserModel::class, MealModel::class, RandomMealModel::class, CustomMealModel::class))
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
    fun getFavoriteMealsByUsername(username: String): List<MealModel>? {
        val user = realm.query<UserModel>("username == $0", username).first().find()
        return user?.listFaveFood
    }

    // Search the favorites of the current user
    fun getFavoriteMealsByName(username: String, mealName: String): List<MealModel>? {
        val user = realm.query<UserModel>("username == $0", username).first().find()
        return user?.listFaveFood?.filter { meal -> meal.name.lowercase().contains(mealName) }
    }

    // Gets the custom meals made by the current user
    fun getCustomMealsByUsername(username: String): List<CustomMealModel>? {
        val user = realm.query<UserModel>("username == $0", username).first().find()
        return user?.listCustomFood
    }

    // Search the custom meals made by the current user
    fun getCustomMealByName(username: String, mealName: String): List<CustomMealModel>? {
        val user = realm.query<UserModel>("username == $0", username).first().find()
        return user?.listCustomFood?.filter { meal -> meal.name.lowercase().contains(mealName) }
    }

    // Gets the information about the custom meals
//    fun searchCustomMeal(mealName: String): {
//        return realm.query<CustomMealModel>("bookName CONTAINS[c] $0", mealName).find()
//    }

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

    // Updates the name of the current user
    suspend fun updateUserName(username: String, newName: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult != null) {
                    val user = findLatest(userResult)

                    user?.apply {
                        this.name = newName
                    }
                }
            }
        }
    }

    // Updates the password of the current user
    suspend fun updatePassword(username: String, newPassword: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult != null) {
                   val user = findLatest(userResult)

                    user?.apply {
                        this.password = newPassword
                    }
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

    // Add custom meal made by the current user
    suspend fun addCustomMeal(username: String, name: String, category: String, ingredient: String, instruction: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult != null) {
                    // Enable this code if you want to stay the meal the same as saved in the database
//                    val mealResult: CustomMealModel? = realm.query<CustomMealModel>("name == $0", name).first().find()
//
//                    if (mealResult == null) {
//                        val meal = CustomMealModel().apply {
//                            this.name = name
//                            this.category = category
//                            this.ingredient = ingredient
//                            this.instruction = instruction
//                            this.source = ""
//                        }
//
//                        val saveMeal = copyToRealm(meal)
//                        if (!findLatest(userResult)?.listCustomFood?.contains(saveMeal)!!) {
//                            findLatest(userResult)?.listCustomFood?.add(saveMeal)
//                        }
//
//                        findLatest(userResult)?.listCustomFood?.add(saveMeal)
//                    } else {
//                        val mealExisting = findLatest(mealResult)
//                        if (!findLatest(userResult)?.listCustomFood?.contains(mealExisting)!!) {
//                            findLatest(userResult)?.listCustomFood?.add(mealExisting!!)
//                        }
//                    }

                    // This just adds the meal regardless of the name
                    val meal = CustomMealModel().apply {
                        this.name = name
                        this.category = category
                        this.ingredient = ingredient
                        this.instruction = instruction
                        this.source = ""
                    }

                    val saveMeal = copyToRealm(meal)
                    findLatest(userResult)?.listCustomFood?.add(saveMeal)
                }
            }
        }
    }

    // Updates the custom meal created by the current user
    suspend fun updateCustomMeal(
        username: String,
        id: String,
        name: String,
        category: String,
        ingredient: String,
        instruction: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult != null) {
                    val mealResult: CustomMealModel? = realm.query<CustomMealModel>("id == $0", ObjectId(id)).first().find()

                    if (mealResult != null) {
                        val mealExisting = findLatest(mealResult)

                        mealExisting?.apply {
                            this.name = name
                            this.category = category
                            this.ingredient = ingredient
                            this.instruction = instruction
                        }
                    }
                }
            }
        }
    }

    // Deletes the custom meal by the current user
    suspend fun deleteCustomMeal(username: String, meal: CustomMeal) {
        withContext(Dispatchers.IO) {
            realm.write {
                val userResult: UserModel? = realm.query<UserModel>("username == $0", username).first().find()

                if (userResult != null) {
                    val mealResult: CustomMealModel? = realm.query<CustomMealModel>("id == $0", ObjectId(meal.id)).first().find()

                    if (mealResult != null) {
                        val mealExisting = findLatest(mealResult)
                        findLatest(userResult)?.listCustomFood?.remove(mealExisting!!)

                        query<CustomMealModel>("id == $0", ObjectId(meal.id))
                            .first()
                            .find()
                            ?.let { delete(it) }
                            ?: throw IllegalStateException("Custom meal not found")
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
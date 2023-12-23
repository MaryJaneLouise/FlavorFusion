package com.mariejuana.flavorfusion.data.constants

object API {
    const val MEAL_BASE_URL = "https://www.themealdb.com"
    const val API_KEY = "1"

    const val MEAL_IMG_URL = "https://www.themealdb.com/images/media/meals/"
    const val INGREDIENTS_IMG_URL = "https://www.themealdb.com/images/ingredients/"

    const val MEAL_GENERATE_RANDOM = "/api/json/v1/1/random.php"
    const val MEAL_CATEGORIES_FULL = "/api/json/v1/1/categories.php"
    const val MEAL_SEARCH_FOOD = "/api/json/v1/1/search.php"
    const val MEAL_SEARCH_ID = "/api/json/v1/1/lookup.php"
    const val MEAL_FILTER_AREA = "/api/json/v1/1/filter.php"

    const val AREA_LIST = "/api/json/v1/1/list.php?a=list"
    const val INGREDIENT_LIST = "/api/json/v1/1/list.php?i=list"
    const val CATEGORY_LIST = "/api/json/v1/1/list.php?c=list"
}
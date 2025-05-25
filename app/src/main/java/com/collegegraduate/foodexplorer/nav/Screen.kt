package com.collegegraduate.foodexplorer.nav

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{foodId}") {
        fun createRoute(foodId: String) = "detail/$foodId"
    }
    object Favourites : Screen("favourites")
}
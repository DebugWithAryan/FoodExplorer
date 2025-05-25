package com.collegegraduate.foodexplorer.dataModels


data class HomeUiState(
    val isLoading: Boolean = false,
    val foodItems: List<FoodItem> = emptyList(),
    val error: String? = null
)

data class DetailUiState(
    val isLoading: Boolean = false,
    val foodItem: FoodItem? = null,
    val error: String? = null,
)

data class FavouritesUiState(
    val isLoading: Boolean = false,
    val favouriteItems: List<FoodItem> = emptyList(),
    val error: String? = null
)
package com.collegegraduate.foodexplorer.dataModels


data class FoodItem(
    val id: String,
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val imageUrl: String,
    val category: String,
    val rating: Float,
    val price: Double,
    val calories: Int,
    val cookingTime: String
)

package com.collegegraduate.foodexplorer.repo

import com.collegegraduate.foodexplorer.apiService.FoodApiService
import com.collegegraduate.foodexplorer.apiService.NetworkModule
import com.collegegraduate.foodexplorer.dataModels.FoodItem

class FoodRepository(
    private val apiService: FoodApiService = NetworkModule.foodApiService
) {


    suspend fun getFoodItem(foodId: String): Result<FoodItem?> {
        return try {
            val response = apiService.getFoodById(foodId)

            when {
                response.isSuccessful -> {
                    val foodItem = response.body()
                    if (foodItem != null) {
                        Result.success(foodItem)
                    } else {
                        Result.failure(Exception("Food item not found"))
                    }
                }
                else -> {
                    Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFoods(): Result<List<FoodItem>> {
        return try {
            val response = apiService.getFoods()

            when {
                response.isSuccessful -> {
                    val foods = response.body()
                    if (foods != null) {
                        Result.success(foods)
                    } else {
                        Result.failure(Exception("No data received"))
                    }
                }
                else -> {
                    Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
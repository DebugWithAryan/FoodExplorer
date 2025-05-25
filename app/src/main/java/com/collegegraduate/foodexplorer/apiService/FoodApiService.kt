package com.collegegraduate.foodexplorer.apiService

import com.collegegraduate.foodexplorer.dataModels.FoodItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodApiService {
    @GET("FoodItems")
    suspend fun getFoods(): Response<List<FoodItem>>

    @GET("FoodItems/{id}")
    suspend fun getFoodById(@Path("id") id: String): Response<FoodItem> // Changed parameter type to String
}
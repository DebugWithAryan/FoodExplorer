package com.collegegraduate.foodexplorer.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.collegegraduate.foodexplorer.FavouritesManager
import com.collegegraduate.foodexplorer.dataModels.HomeUiState
import com.collegegraduate.foodexplorer.repo.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodRepository()
    private val favouritesManager = FavouritesManager(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val favouriteIds = favouritesManager.favouriteIds

    init {
        loadFoodItems()
    }

    fun loadFoodItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = repository.getFoods()

                when {
                    result.isSuccess -> {
                        val foodItems = result.getOrNull() ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            foodItems = foodItems,
                            error = null
                        )
                    }
                    result.isFailure -> {
                        val exception = result.exceptionOrNull()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            foodItems = emptyList(),
                            error = exception?.message ?: "Unknown error occurred"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    foodItems = emptyList(),
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun toggleFavourite(foodId: String) {
        viewModelScope.launch {
            try {
                val currentFavourites = favouritesManager.favouriteIds.toString()

                if (currentFavourites.contains(foodId)) {
                    favouritesManager.removeFavourite(foodId)
                } else {
                    favouritesManager.addFavourite(foodId)
                }
            } catch (e: Exception) {
                println("Error toggling favourite: ${e.message}")
            }
        }
    }
}
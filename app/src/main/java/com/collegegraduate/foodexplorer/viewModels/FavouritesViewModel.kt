package com.collegegraduate.foodexplorer.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.collegegraduate.foodexplorer.FavouritesManager
import com.collegegraduate.foodexplorer.dataModels.FavouritesUiState
import com.collegegraduate.foodexplorer.dataModels.FoodItem
import com.collegegraduate.foodexplorer.repo.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodRepository()
    private val favouritesManager = FavouritesManager(application)

    private val _uiState = MutableStateFlow(FavouritesUiState())
    val uiState: StateFlow<FavouritesUiState> = _uiState.asStateFlow()

    val favouriteIds = favouritesManager.favouriteIds

    private var cachedFoodItems: List<FoodItem> = emptyList()
    private var isInitialized = false

    init {
        observeFavouriteItems()
    }

    private fun observeFavouriteItems() {
        if (isInitialized) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                if (cachedFoodItems.isEmpty()) {
                    val foodItemsResult = repository.getFoods()

                    if (foodItemsResult.isSuccess) {
                        cachedFoodItems = foodItemsResult.getOrNull() ?: emptyList()
                    } else {
                        val exception = foodItemsResult.exceptionOrNull()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            favouriteItems = emptyList(),
                            error = exception?.message ?: "Failed to load food items"
                        )
                        return@launch
                    }
                }

                isInitialized = true
                favouriteIds
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error loading favourites"
                        )
                    }
                    .collect { favIds ->
                        val favouriteItems = cachedFoodItems.filter { foodItem ->
                            favIds.contains(foodItem.id)
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            favouriteItems = favouriteItems,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    favouriteItems = emptyList(),
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun removeFavourite(foodId: String) {
        viewModelScope.launch {
            try {
                favouritesManager.removeFavourite(foodId)
            } catch (e: Exception) {
                println("Error removing favourite: ${e.message}")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        isInitialized = false
        cachedFoodItems = emptyList()
    }
}
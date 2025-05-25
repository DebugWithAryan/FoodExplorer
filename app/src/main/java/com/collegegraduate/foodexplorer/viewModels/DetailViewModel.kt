package com.collegegraduate.foodexplorer.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.collegegraduate.foodexplorer.FavouritesManager
import com.collegegraduate.foodexplorer.dataModels.DetailUiState
import com.collegegraduate.foodexplorer.repo.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodRepository()
    private val favouritesManager = FavouritesManager(application)

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    val favouriteIds = favouritesManager.favouriteIds

    fun loadFoodItem(foodId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val result = repository.getFoodItem(foodId)

                if (result.isSuccess) {
                    val foodItem = result.getOrNull()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        foodItem = foodItem,
                        error = null
                    )
                } else {
                    val exception = result.exceptionOrNull()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        foodItem = null,
                        error = exception?.message ?: "Unknown error occurred"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    foodItem = null,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun toggleFavourite(foodId: String) {
        viewModelScope.launch {
            val currentFavourites = favouriteIds.toString()
            if (currentFavourites.contains(foodId)) {
                favouritesManager.removeFavourite(foodId)
            } else {
                favouritesManager.addFavourite(foodId)
            }
        }
    }
}
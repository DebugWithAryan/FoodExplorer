package com.collegegraduate.foodexplorer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favourites")

class FavouritesManager(private val context: Context) {

    companion object {
        private val FAVOURITES_KEY = stringSetPreferencesKey("favourite_food_ids")
    }

    val favouriteIds: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVOURITES_KEY] ?: emptySet()
        }

    suspend fun addFavourite(foodId: String) {
        context.dataStore.edit { preferences ->
            val currentFavourites = preferences[FAVOURITES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentFavourites.add(foodId)
            preferences[FAVOURITES_KEY] = currentFavourites
        }
    }

    suspend fun removeFavourite(foodId: String) {
        context.dataStore.edit { preferences ->
            val currentFavourites = preferences[FAVOURITES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentFavourites.remove(foodId)
            preferences[FAVOURITES_KEY] = currentFavourites
        }
    }
}
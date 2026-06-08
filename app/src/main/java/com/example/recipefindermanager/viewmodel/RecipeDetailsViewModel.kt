package com.example.recipefindermanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipefindermanager.data.Recipe
import com.example.recipefindermanager.data.RecipeRepository
import com.example.recipefindermanager.data.SampleRecipes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeDetailsViewModel : ViewModel() {

    private val repo = RecipeRepository

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadRecipe(id: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _recipe.value = repo.getById(id) ?: SampleRecipes.getById(id)
            } catch (e: Exception) {
                val sampleRecipe = SampleRecipes.getById(id)
                _recipe.value = sampleRecipe
                _error.value = if (sampleRecipe == null) e.message ?: "Unknown error" else null
            } finally {
                _loading.value = false
            }
        }
    }

}
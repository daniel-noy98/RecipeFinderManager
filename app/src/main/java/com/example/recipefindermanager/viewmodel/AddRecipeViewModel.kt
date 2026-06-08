package com.example.recipefindermanager.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipefindermanager.data.Recipe
import com.example.recipefindermanager.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddRecipeViewModel : ViewModel() {

    private val _editRecipe = MutableStateFlow<Recipe?>(null)
    val editRecipe: StateFlow<Recipe?> = _editRecipe

    fun loadForEdit(recipeId: String) {
        viewModelScope.launch {
            _editRecipe.value = RecipeRepository.getById(recipeId)
        }
    }

    fun addRecipe(
        title: String,
        description: String,
        category: String,
        cookTime: String,
        servings: Int,
        imageUrl: String?,
        imageUri: Uri?,
        ingredients: List<String>,
        instructions: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val finalImageUrl = resolveImageUrl(imageUrl, imageUri)

                RecipeRepository.addRecipe(
                    Recipe(
                        name = title.trim(),
                        description = description.trim(),
                        category = category.trim(),
                        cookTime = cookTime.trim(),
                        servings = servings,
                        imageUrl = finalImageUrl,
                        ingredients = ingredients,
                        instructions = instructions.trim(),
                        public = true
                    )
                )
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error")
            }
        }
    }

    fun updateRecipe(
        recipeId: String,
        title: String,
        description: String,
        category: String,
        cookTime: String,
        servings: Int,
        imageUrl: String?,
        imageUri: Uri?,
        existingImageUrl: String?,
        ingredients: List<String>,
        instructions: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val finalImageUrl = when {
                    imageUri != null -> RecipeRepository.uploadRecipeImage(imageUri)
                    !imageUrl.isNullOrBlank() -> imageUrl
                    else -> existingImageUrl
                }

                RecipeRepository.updateRecipe(
                    Recipe(
                        id = recipeId,
                        name = title.trim(),
                        description = description.trim(),
                        category = category.trim(),
                        cookTime = cookTime.trim(),
                        servings = servings,
                        imageUrl = finalImageUrl,
                        ingredients = ingredients,
                        instructions = instructions.trim(),
                        public = true
                    )
                )
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error")
            }
        }
    }

    private suspend fun resolveImageUrl(imageUrl: String?, imageUri: Uri?): String? {
        return when {
            imageUri != null -> RecipeRepository.uploadRecipeImage(imageUri)
            imageUrl.isNullOrBlank() -> null
            else -> imageUrl
        }
    }
}

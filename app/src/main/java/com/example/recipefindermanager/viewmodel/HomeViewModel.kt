package com.example.recipefindermanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipefindermanager.data.FavoritesStore
import com.example.recipefindermanager.data.Recipe
import com.example.recipefindermanager.data.RecipeRepository
import com.example.recipefindermanager.data.SampleRecipes
import com.example.recipefindermanager.ui.screens.home.RecipeUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repo = RecipeRepository

    private val _recipes = MutableStateFlow<List<RecipeUiModel>>(emptyList())
    val recipes: StateFlow<List<RecipeUiModel>> = _recipes

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _myRecipes = MutableStateFlow<List<RecipeUiModel>>(emptyList())
    val myRecipes: StateFlow<List<RecipeUiModel>> = _myRecipes

    private val _myRecipesLoading = MutableStateFlow(false)
    val myRecipesLoading: StateFlow<Boolean> = _myRecipesLoading

    private val _myRecipesError = MutableStateFlow<String?>(null)
    val myRecipesError: StateFlow<String?> = _myRecipesError

    init {
        refresh()
        refreshMyRecipes()
    }

    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val list = repo.getAllPublicRecipes()
                _recipes.value = mergeWithSamples(list).map { it.toUiModel() }
            } catch (e: Exception) {
                _error.value = null
                _recipes.value = SampleRecipes.recipes.map { it.toUiModel() }
            } finally {
                _loading.value = false
            }
        }
    }

    fun refreshMyRecipes() {
        viewModelScope.launch {
            _myRecipesLoading.value = true
            _myRecipesError.value = null
            try {
                val list = repo.getMyRecipes()
                _myRecipes.value = list.map { it.toUiModel() }
            } catch (e: Exception) {
                _myRecipesError.value = e.message ?: "Could not load your recipes"
            } finally {
                _myRecipesLoading.value = false
            }
        }
    }

    fun isFavorite(recipeId: String): Boolean {
        return _recipes.value.firstOrNull { it.id == recipeId }?.isFavorite == true
    }

    fun removeRecipe(recipeId: String) {
        _recipes.value = _recipes.value.filter { it.id != recipeId }
        _myRecipes.value = _myRecipes.value.filter { it.id != recipeId }
    }

    fun deleteRecipe(
        recipeId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repo.deleteRecipe(recipeId)
                removeRecipe(recipeId)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Could not delete recipe")
            }
        }
    }

    fun toggleFavorite(recipeId: String) {
        val current = _recipes.value.firstOrNull { it.id == recipeId }
            ?: _myRecipes.value.firstOrNull { it.id == recipeId }
            ?: return
        val nextFavorite = !current.isFavorite

        _recipes.value = _recipes.value.map { recipe ->
            if (recipe.id == recipeId) recipe.copy(isFavorite = nextFavorite) else recipe
        }
        _myRecipes.value = _myRecipes.value.map { recipe ->
            if (recipe.id == recipeId) recipe.copy(isFavorite = nextFavorite) else recipe
        }

        if (recipeId.startsWith("sample-")) {
            FavoritesStore.setFavorite(recipeId, nextFavorite)
            return
        }

        viewModelScope.launch {
            try {
                repo.toggleFavorite(recipeId, nextFavorite)
            } catch (e: Exception) {
                _recipes.value = _recipes.value.map { recipe ->
                    if (recipe.id == recipeId) recipe.copy(isFavorite = !nextFavorite) else recipe
                }
                _myRecipes.value = _myRecipes.value.map { recipe ->
                    if (recipe.id == recipeId) recipe.copy(isFavorite = !nextFavorite) else recipe
                }
                _error.value = e.message ?: "Could not update favorite"
            }
        }
    }
}

private fun mergeWithSamples(remote: List<Recipe>): List<Recipe> {
    val sampleIds = SampleRecipes.recipes.map { it.id }.toSet()
    val validRemote = remote.filter { recipe ->
        recipe.name.isNotBlank() &&
                recipe.id.isNotBlank() &&
                recipe.id !in sampleIds
    }
    return SampleRecipes.recipes + validRemote
}

private fun Recipe.toUiModel(): RecipeUiModel {
    return RecipeUiModel(
        id = id,
        name = name,
        category = category,
        cookTime = cookTime,
        servings = servings,
        imageUrl = imageUrl ?: "",
        ingredients = ingredients.filter { it.isNotBlank() },
        instructions = instructions.ifBlank { "" },
        isFavorite = isFavorite || FavoritesStore.isFavorite(id)
    )
}

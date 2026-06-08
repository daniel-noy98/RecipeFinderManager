package com.example.recipefindermanager.ui.screens.home

data class RecipeUiModel(
    val id: String,
    val name: String,
    val category: String,
    val cookTime: String,
    val servings: Int,
    val imageUrl: String,
    val ingredients: List<String> = emptyList(),
    val instructions: String = "",
    val isFavorite: Boolean = false
)

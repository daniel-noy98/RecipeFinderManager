package com.example.recipefindermanager.ui.components

import com.example.recipefindermanager.R

object RecipeImages {
    fun resIdFor(recipeId: String): Int? = when (recipeId) {
        "sample-carbonara" -> R.drawable.sample_carbonara
        "sample-pancakes" -> R.drawable.sample_pancakes
        "sample-quinoa-salad" -> R.drawable.sample_quinoa
        else -> null
    }

    fun modelFor(recipeId: String, imageUrl: String?): Any? {
        return resIdFor(recipeId) ?: imageUrl?.takeIf { it.isNotBlank() }
    }
}

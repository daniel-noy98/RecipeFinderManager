package com.example.recipefindermanager.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Recipe(
    val id: String = "",

    @get:PropertyName("title")
    @set:PropertyName("title")
    var name: String = "",

    val description: String = "",
    val category: String = "",
    val cookTime: String = "",
    val servings: Int = 1,

    val imageUrl: String? = null,
    val ingredients: List<String> = emptyList(),
    val instructions: String = "",

    val ownerId: String = "",
    val public: Boolean = true,

    @get:Exclude
    val isFavorite: Boolean = false,
    val favoriteUserIds: List<String> = emptyList(),

    val createdAtMillis: Long = 0L,
    val updatedAtMillis: Long = 0L
)
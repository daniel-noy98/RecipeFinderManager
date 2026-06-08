package com.example.recipefindermanager.data

object FavoritesStore {
    private val favoriteIds = mutableSetOf<String>()

    fun isFavorite(id: String): Boolean = id in favoriteIds

    fun setFavorite(id: String, favorite: Boolean) {
        if (favorite) favoriteIds.add(id) else favoriteIds.remove(id)
    }

    fun clear() {
        favoriteIds.clear()
    }
}

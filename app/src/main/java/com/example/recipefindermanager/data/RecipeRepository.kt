package com.example.recipefindermanager.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

object RecipeRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private fun recipesCol() = db.collection("recipes")

    fun currentUserId(): String? = auth.currentUser?.uid

    suspend fun getAllPublicRecipes(): List<Recipe> {
        val snap = recipesCol()
            .whereEqualTo("public", true)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toRecipe() }
    }

    suspend fun getMyRecipes(): List<Recipe> {
        val uid = currentUserId() ?: return emptyList()

        val snap = recipesCol()
            .whereEqualTo("ownerId", uid)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toRecipe() }
    }

    suspend fun getById(id: String): Recipe? {
        if (id.isBlank()) return null
        val doc = recipesCol().document(id).get().await()
        if (!doc.exists()) return null
        return doc.toRecipe()
    }

    suspend fun addRecipe(recipe: Recipe): String {
        val uid = currentUserId() ?: throw IllegalStateException("User not logged in")

        val id = if (recipe.id.isNotBlank()) recipe.id else UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val toSave = recipe.copy(
            id = id,
            ownerId = uid,
            createdAtMillis = now,
            updatedAtMillis = now
        )

        recipesCol().document(id).set(toSave).await()
        return id
    }

    suspend fun uploadRecipeImage(imageUri: Uri): String {
        val uid = currentUserId() ?: throw IllegalStateException("User not logged in")
        val imageId = UUID.randomUUID().toString()
        val ref = storage.reference.child("recipe-images/$uid/$imageId.jpg")

        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }

    fun isOwner(recipe: Recipe): Boolean {
        val uid = currentUserId() ?: return false
        return recipe.ownerId.isNotBlank() && recipe.ownerId == uid
    }

    suspend fun updateRecipe(recipe: Recipe) {
        val existing = getById(recipe.id) ?: throw IllegalStateException("Recipe not found")
        if (!isOwner(existing)) throw IllegalStateException("You can only edit your own recipes")

        val updated = recipe.copy(
            ownerId = existing.ownerId,
            createdAtMillis = existing.createdAtMillis,
            favoriteUserIds = existing.favoriteUserIds,
            updatedAtMillis = System.currentTimeMillis()
        )

        recipesCol().document(recipe.id).set(updated).await()
    }

    suspend fun deleteRecipe(id: String) {
        val existing = getById(id) ?: throw IllegalStateException("Recipe not found")
        if (!isOwner(existing)) throw IllegalStateException("You can only delete your own recipes")
        recipesCol().document(id).delete().await()
    }

    suspend fun toggleFavorite(id: String, favorite: Boolean) {
        val uid = currentUserId() ?: throw IllegalStateException("User not logged in")
        val docRef = recipesCol().document(id)

        docRef.update(
            mapOf(
                "favoriteUserIds" to if (favorite) FieldValue.arrayUnion(uid) else FieldValue.arrayRemove(uid),
                "updatedAtMillis" to System.currentTimeMillis()
            )
        ).await()
    }

    private fun DocumentSnapshot.toRecipe(): Recipe? {
        if (!exists() || id.isBlank()) return null

        return try {
            val raw = toObject(Recipe::class.java)

            val name = raw?.name?.takeIf { it.isNotBlank() }
                ?: getString("title")
                ?: getString("name")
                ?: ""

            if (name.isBlank()) return null

            Recipe(
                id = id,
                name = name,
                description = raw?.description?.takeIf { it.isNotBlank() } ?: getString("description") ?: "",
                category = raw?.category?.takeIf { it.isNotBlank() } ?: getString("category") ?: "",
                cookTime = raw?.cookTime?.takeIf { it.isNotBlank() } ?: getString("cookTime") ?: "",
                servings = when {
                    (raw?.servings ?: 0) > 0 -> raw!!.servings
                    else -> getLong("servings")?.toInt() ?: 1
                },
                imageUrl = raw?.imageUrl?.takeIf { it.isNotBlank() } ?: getString("imageUrl"),
                ingredients = readStringList("ingredients", raw?.ingredients ?: emptyList()),
                instructions = raw?.instructions?.takeIf { it.isNotBlank() } ?: getString("instructions") ?: "",
                ownerId = raw?.ownerId?.takeIf { it.isNotBlank() } ?: getString("ownerId") ?: "",
                public = raw?.public ?: getBoolean("public") ?: true,
                favoriteUserIds = readStringList("favoriteUserIds", raw?.favoriteUserIds ?: emptyList()),
                createdAtMillis = raw?.createdAtMillis ?: getLong("createdAtMillis") ?: 0L,
                updatedAtMillis = raw?.updatedAtMillis ?: getLong("updatedAtMillis") ?: 0L
            ).withCurrentUserFavorite()
        } catch (_: Exception) {
            null
        }
    }

    private fun DocumentSnapshot.readStringList(
        field: String,
        fallback: List<String>
    ): List<String> {
        when (val value = get(field)) {
            is List<*> -> {
                val fromList = value.mapNotNull { item ->
                    when (item) {
                        is String -> item.trim().takeIf { it.isNotBlank() }
                        else -> item?.toString()?.trim()?.takeIf { it.isNotBlank() }
                    }
                }
                if (fromList.isNotEmpty()) return fromList
            }
            is String -> {
                val fromString = value.split("\n", ",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                if (fromString.isNotEmpty()) return fromString
            }
        }
        return fallback.filter { it.isNotBlank() }
    }

    private fun Recipe.withCurrentUserFavorite(): Recipe {
        val uid = currentUserId()
        return copy(isFavorite = uid != null && favoriteUserIds.contains(uid))
    }
}

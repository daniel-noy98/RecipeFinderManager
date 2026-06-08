package com.example.recipefindermanager.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.recipefindermanager.ui.components.RecipeImages

@Composable
fun RecipeCard(
    recipe: RecipeUiModel,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clickable(onClick = onClick)
            ) {
            val imageModel = RecipeImages.modelFor(recipe.id, recipe.imageUrl)
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = recipe.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No image", color = Color(0xFF6B7280))
                }
            }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (recipe.isFavorite) Color(0xFFEF4444) else Color(0xFF6B7280)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .padding(14.dp)
            ) {
                Surface(
                    color = Color(0xFFFFF7ED),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = recipe.category,
                        color = Color(0xFFEA580C),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = "Time",
                            tint = Color(0xFF6B7280)
                        )
                        Text(recipe.cookTime, color = Color(0xFF6B7280), style = MaterialTheme.typography.bodySmall)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = "Servings",
                            tint = Color(0xFF6B7280)
                        )
                        Text("${recipe.servings} servings", color = Color(0xFF6B7280), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

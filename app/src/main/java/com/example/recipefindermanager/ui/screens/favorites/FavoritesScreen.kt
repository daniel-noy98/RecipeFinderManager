package com.example.recipefindermanager.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipefindermanager.ui.screens.home.RecipeCard
import com.example.recipefindermanager.viewmodel.HomeViewModel

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onRecipeClick: (String) -> Unit = {},
    vm: HomeViewModel = viewModel()
) {
    val recipes by vm.recipes.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val favorites = recipes.filter { it.isFavorite }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val bgBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFF1F8E9), Color(0xFFFFF3E0))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (error != null) {
            Text(
                text = error ?: "Could not load favorites",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (favorites.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No favorites yet", color = Color(0xFF6B7280))
                    }
                }
            } else {
                items(favorites, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        onFavoriteClick = { vm.toggleFavorite(recipe.id) }
                    )
                }
            }
        }
    }
}

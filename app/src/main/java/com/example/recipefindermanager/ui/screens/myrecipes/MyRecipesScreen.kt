package com.example.recipefindermanager.ui.screens.myrecipes

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
fun MyRecipesScreen(
    modifier: Modifier = Modifier,
    onRecipeClick: (String) -> Unit = {},
    onAddRecipe: () -> Unit = {},
    vm: HomeViewModel = viewModel()
) {
    val myRecipes by vm.myRecipes.collectAsState()
    val loading by vm.myRecipesLoading.collectAsState()
    val error by vm.myRecipesError.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.refreshMyRecipes()
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
            text = "My Recipes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Recipes you created and shared",
            color = Color(0xFF6B7280),
            style = MaterialTheme.typography.bodyMedium,
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
                text = error ?: "Failed to load recipes",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (myRecipes.isEmpty() && !loading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("You have not added any recipes yet", color = Color(0xFF6B7280))
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onAddRecipe) {
                            Text("Add your first recipe")
                        }
                    }
                }
            } else {
                items(myRecipes, key = { it.id }) { recipe ->
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

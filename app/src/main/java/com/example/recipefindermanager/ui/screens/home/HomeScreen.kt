package com.example.recipefindermanager.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.recipefindermanager.R
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipefindermanager.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onRecipeClick: (String) -> Unit = {},
    vm: HomeViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val recipesUi by vm.recipes.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
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

    val filtered = remember(query, recipesUi) {
        val q = query.trim()
        if (q.isEmpty()) recipesUi
        else recipesUi.filter {
            it.name.contains(q, true) ||
                    it.category.contains(q, true)
        }
    }

    val bgBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFF1F8E9), Color(0xFFFFF3E0))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        HeaderWithSearch(query) { query = it }

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
                text = error ?: "Could not load recipes",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No recipes found")
                    }
                }
            } else {
                items(filtered, key = { it.id }) { recipe ->
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

@Composable
private fun HeaderWithSearch(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF22C55E))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "App logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Recipe Finder & Manager",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search recipes...") },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
        )
    }
}
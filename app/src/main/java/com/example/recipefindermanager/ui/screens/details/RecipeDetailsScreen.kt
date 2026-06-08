package com.example.recipefindermanager.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.recipefindermanager.data.RecipeRepository
import com.example.recipefindermanager.ui.components.RecipeImages
import com.example.recipefindermanager.viewmodel.HomeViewModel
import com.example.recipefindermanager.viewmodel.RecipeDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    recipeId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    homeViewModel: HomeViewModel,
    key: String = recipeId,
    vm: RecipeDetailsViewModel = viewModel(key = key)
) {
    val recipe by vm.recipe.collectAsState()
    val recipes by homeViewModel.recipes.collectAsState()
    val isFavorite = recipes.firstOrNull { it.id == recipeId }?.isFavorite == true
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(recipeId) {
        vm.loadRecipe(recipeId)
    }

    DisposableEffect(lifecycleOwner, recipeId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.loadRecipe(recipeId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val bgBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFF1F8E9), Color(0xFFFFF3E0))
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!deleting) showDeleteDialog = false },
            title = { Text("Delete recipe?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleting = true
                        deleteError = null
                        homeViewModel.deleteRecipe(
                            recipeId = recipeId,
                            onSuccess = {
                                deleting = false
                                showDeleteDialog = false
                                onBack()
                            },
                            onError = { message ->
                                deleting = false
                                deleteError = message
                            }
                        )
                    },
                    enabled = !deleting
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !deleting
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.name ?: "Recipe Details") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    if (recipe != null) {
                        IconButton(onClick = { homeViewModel.toggleFavorite(recipeId) }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(bgBrush)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            if (error != null) {
                Text(error ?: "Could not load recipe", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
            }

            val currentRecipe = recipe
            if (currentRecipe == null) {
                Text("Recipe not found (id=$recipeId)")
                return@Column
            }

            val isOwner = RecipeRepository.isOwner(currentRecipe)

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    val imageModel = RecipeImages.modelFor(currentRecipe.id, currentRecipe.imageUrl)
                    if (imageModel != null) {
                        AsyncImage(
                            model = imageModel,
                            contentDescription = currentRecipe.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                .background(Color(0xFFE5E7EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No image", color = Color(0xFF6B7280))
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = currentRecipe.category,
                                color = Color(0xFFEA580C),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(currentRecipe.name, style = MaterialTheme.typography.headlineSmall)

                        if (currentRecipe.description.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(currentRecipe.description, color = Color(0xFF6B7280))
                        }

                        Spacer(Modifier.height(12.dp))

                        Text("Cook time: ${currentRecipe.cookTime}", color = Color(0xFF374151))
                        Spacer(Modifier.height(6.dp))
                        Text("Servings: ${currentRecipe.servings}", color = Color(0xFF374151))

                        Spacer(Modifier.height(16.dp))

                        Text("Ingredients", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        val ingredients = currentRecipe.ingredients.filter { it.isNotBlank() }
                        if (ingredients.isEmpty()) {
                            Text("No ingredients yet", color = Color(0xFF6B7280))
                        } else {
                            ingredients.forEach { ing ->
                                Text("• $ing", color = Color(0xFF374151))
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text("Instructions", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = currentRecipe.instructions.ifBlank { "No instructions yet" },
                            color = Color(0xFF374151)
                        )
                    }
                }
            }

            if (isOwner) {
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onEdit(recipeId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }

            deleteError?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

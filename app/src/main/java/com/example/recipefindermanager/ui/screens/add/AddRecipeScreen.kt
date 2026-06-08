package com.example.recipefindermanager.ui.screens.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.recipefindermanager.ui.components.RecipeImages
import com.example.recipefindermanager.viewmodel.AddRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    recipeId: String?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    vm: AddRecipeViewModel = viewModel()
) {
    val isEditMode = recipeId != null
    val editRecipe by vm.editRecipe.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var cookTime by remember { mutableStateOf("") }
    var servingsText by remember { mutableStateOf("1") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var ingredientsText by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }

    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(recipeId) {
        if (recipeId != null) vm.loadForEdit(recipeId)
    }

    LaunchedEffect(editRecipe) {
        val recipe = editRecipe ?: return@LaunchedEffect
        title = recipe.name
        description = recipe.description
        category = recipe.category
        cookTime = recipe.cookTime
        servingsText = recipe.servings.toString()
        imageUrl = recipe.imageUrl ?: ""
        existingImageUrl = recipe.imageUrl
        ingredientsText = recipe.ingredients.joinToString(", ")
        instructions = recipe.instructions
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Recipe" else "Add Recipe") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cookTime,
                onValueChange = { cookTime = it },
                label = { Text("Cook Time") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = servingsText,
                onValueChange = { servingsText = it },
                label = { Text("Servings") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL (external)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = { imagePicker.launch("image/*") },
                enabled = !saving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedImageUri == null) "Choose Image from Device" else "Change Selected Image")
            }

            val imagePreview = selectedImageUri
                ?: imageUrl.takeIf { it.isNotBlank() }
                ?: existingImageUrl?.let { RecipeImages.modelFor(recipeId ?: "", it) }

            if (imagePreview != null) {
                AsyncImage(
                    model = imagePreview,
                    contentDescription = "Recipe image preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            OutlinedTextField(
                value = ingredientsText,
                onValueChange = { ingredientsText = it },
                label = { Text("Ingredients (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            val servings = servingsText.toIntOrNull()?.coerceAtLeast(1) ?: 1
            val canSave = title.isNotBlank() &&
                    category.isNotBlank() &&
                    instructions.isNotBlank() &&
                    servingsText.toIntOrNull() != null &&
                    (!isEditMode || editRecipe != null)

            Button(
                onClick = {
                    saving = true
                    error = null

                    val ingredients = ingredientsText
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }

                    val onSuccess = {
                        saving = false
                        onSaved()
                    }
                    val onError = { message: String ->
                        error = message
                        saving = false
                    }

                    if (isEditMode && recipeId != null) {
                        vm.updateRecipe(
                            recipeId = recipeId,
                            title = title,
                            description = description,
                            category = category,
                            cookTime = cookTime,
                            servings = servings,
                            imageUrl = imageUrl.ifBlank { null },
                            imageUri = selectedImageUri,
                            existingImageUrl = existingImageUrl,
                            ingredients = ingredients,
                            instructions = instructions,
                            onSuccess = onSuccess,
                            onError = onError
                        )
                    } else {
                        vm.addRecipe(
                            title = title,
                            description = description,
                            category = category,
                            cookTime = cookTime,
                            servings = servings,
                            imageUrl = imageUrl.ifBlank { null },
                            imageUri = selectedImageUri,
                            ingredients = ingredients,
                            instructions = instructions,
                            onSuccess = onSuccess,
                            onError = onError
                        )
                    }
                },
                enabled = canSave && !saving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (saving) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(if (isEditMode) "Update" else "Save")
                }
            }

            if (!canSave && !saving) {
                Text(
                    text = "Please fill title, category, instructions and a valid servings number.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

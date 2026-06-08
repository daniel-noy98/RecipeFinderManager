package com.example.recipefindermanager.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.recipefindermanager.ui.navigation.AppNavGraph
import com.example.recipefindermanager.ui.navigation.Routes
import com.example.recipefindermanager.viewmodel.HomeViewModel

private enum class BottomTab { Recipes, MyRecipes, Favorites }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()
    var selectedTab by remember { mutableStateOf(BottomTab.Recipes) }

    fun goHome() {
        selectedTab = BottomTab.Recipes
        navController.navigate(Routes.HOME) {
            popUpTo(navController.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun goMyRecipes() {
        selectedTab = BottomTab.MyRecipes
        navController.navigate(Routes.MY_RECIPES) {
            popUpTo(navController.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun goFavorites() {
        selectedTab = BottomTab.Favorites
        navController.navigate(Routes.FAVORITES) {
            popUpTo(navController.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Finder & Manager") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                selectedTab = selectedTab,
                onSelect = {
                    when (it) {
                        BottomTab.Recipes -> goHome()
                        BottomTab.MyRecipes -> goMyRecipes()
                        BottomTab.Favorites -> goFavorites()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.ADD) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            AppNavGraph(
                navController = navController,
                homeViewModel = homeViewModel,
                onOpenAdd = { navController.navigate(Routes.ADD) },
                onOpenDetails = { id -> navController.navigate(Routes.details(id)) }
            )
        }
    }
}

@Composable
private fun BottomBar(
    selectedTab: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == BottomTab.Recipes,
            onClick = { onSelect(BottomTab.Recipes) },
            icon = { Icon(Icons.AutoMirrored.Outlined.MenuBook, contentDescription = "Recipes") },
            label = { Text("Recipes") }
        )

        NavigationBarItem(
            selected = selectedTab == BottomTab.MyRecipes,
            onClick = { onSelect(BottomTab.MyRecipes) },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "My Recipes") },
            label = { Text("My Recipes") }
        )

        NavigationBarItem(
            selected = selectedTab == BottomTab.Favorites,
            onClick = { onSelect(BottomTab.Favorites) },
            icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorites") },
            label = { Text("Favorites") }
        )
    }
}
package com.example.recipefindermanager.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.recipefindermanager.ui.screens.add.AddRecipeScreen
import com.example.recipefindermanager.ui.screens.details.RecipeDetailsScreen
import com.example.recipefindermanager.ui.screens.favorites.FavoritesScreen
import com.example.recipefindermanager.ui.screens.home.HomeScreen
import com.example.recipefindermanager.ui.screens.myrecipes.MyRecipesScreen
import com.example.recipefindermanager.viewmodel.HomeViewModel

object Routes {
    const val HOME = "home"
    const val MY_RECIPES = "my_recipes"
    const val FAVORITES = "favorites"
    const val ADD = "add"
    const val EDIT = "edit/{recipeId}"
    const val DETAILS = "details/{recipeId}"

    fun details(recipeId: String) = "details/${Uri.encode(recipeId)}"
    fun edit(recipeId: String) = "edit/${Uri.encode(recipeId)}"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    onOpenAdd: () -> Unit,
    onOpenDetails: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                vm = homeViewModel,
                onRecipeClick = { id ->
                    if (id.isNotBlank()) onOpenDetails(id)
                }
            )
        }

        composable(Routes.MY_RECIPES) {
            MyRecipesScreen(
                vm = homeViewModel,
                onRecipeClick = { id ->
                    if (id.isNotBlank()) onOpenDetails(id)
                },
                onAddRecipe = onOpenAdd
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                vm = homeViewModel,
                onRecipeClick = { id ->
                    if (id.isNotBlank()) onOpenDetails(id)
                }
            )
        }

        composable(Routes.ADD) {
            AddRecipeScreen(
                recipeId = null,
                onBack = { navController.popBackStack() },
                onSaved = {
                    homeViewModel.refresh()
                    homeViewModel.refreshMyRecipes()
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = Uri.decode(backStackEntry.arguments?.getString("recipeId").orEmpty())
            AddRecipeScreen(
                recipeId = id,
                onBack = { navController.popBackStack() },
                onSaved = {
                    homeViewModel.refresh()
                    homeViewModel.refreshMyRecipes()
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = Uri.decode(backStackEntry.arguments?.getString("recipeId").orEmpty())
            RecipeDetailsScreen(
                recipeId = id,
                homeViewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onEdit = { recipeId -> navController.navigate(Routes.edit(recipeId)) },
                key = id
            )
        }
    }
}

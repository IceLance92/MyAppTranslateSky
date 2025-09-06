package com.example.myapptranclatesky.ux

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

object Destinations {
    const val HOME = "home"
    const val FAVORITES = "favorites"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Destinations.HOME) {
        composable(Destinations.HOME) { HomeScreen(navController) }
        composable(Destinations.FAVORITES) { FavoritesScreen(navController) }
    }
}
package io.github.rsookram.rss

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.rsookram.rss.feeds.FeedsScreen
import io.github.rsookram.rss.home.HomeScreen

@Composable
fun Main() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }

        composable("feeds") { FeedsScreen(navController) }
    }
}

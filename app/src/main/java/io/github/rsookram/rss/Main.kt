package io.github.rsookram.rss

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.rsookram.rss.feeds.Feeds
import io.github.rsookram.rss.home.Home

/**
 * The entry point into the UI, implemented in compose.
 */
@Composable
fun Main() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        composable("home") { Home(navController) }

        composable("feeds") { Feeds(navController) }
    }
}

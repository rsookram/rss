package io.github.rsookram.rss.feeds

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.rsookram.rss.data.Repository
import javax.inject.Inject

@HiltViewModel
class FeedsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val feeds = repository.feeds()
}

@Composable
fun FeedsScreen(navController: NavController, vm: FeedsViewModel = hiltViewModel()) {
    val feeds = vm.feeds.collectAsState(emptyList())

    Feeds(feeds.value)
}

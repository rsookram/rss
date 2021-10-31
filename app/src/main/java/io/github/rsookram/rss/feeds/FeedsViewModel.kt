package io.github.rsookram.rss.feeds

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.rsookram.rss.Feed
import io.github.rsookram.rss.data.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val feeds = repository.feeds()

    // TODO: Switch to app-level scope for these operations
    fun onAddFeed(url: String) {
        viewModelScope.launch {
            repository.addFeed(url)
        }
    }

    fun onDeleteFeed(feed: Feed) {
        viewModelScope.launch {
            repository.removeFeed(feed.id)
        }
    }
}

@Composable
fun FeedsScreen(navController: NavController, vm: FeedsViewModel = hiltViewModel()) {
    val feeds = vm.feeds.collectAsState(emptyList())

    Feeds(
        feeds.value,
        vm::onAddFeed,
        vm::onDeleteFeed,
        onUpClick = { navController.popBackStack() },
    )
}

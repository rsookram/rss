package io.github.rsookram.rss.feeds

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.rsookram.rss.ApplicationScope
import io.github.rsookram.rss.Feed
import io.github.rsookram.rss.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [Feeds]
 */
@HiltViewModel
class FeedsViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    val feeds = repository.feeds()

    fun onAddFeed(url: String) {
        appScope.launch {
            if (repository.addFeed(url)) {
                // TODO: Show message on success
            } else {
                // TODO: Show error if feed loading fails
            }
        }
    }

    fun onDeleteFeed(feed: Feed) {
        appScope.launch {
            repository.removeFeed(feed.id)
        }
    }
}

/**
 * The stateful version of [Feeds]
 */
@Composable
fun Feeds(navController: NavController, vm: FeedsViewModel = hiltViewModel()) {
    val feeds = vm.feeds.collectAsState(emptyList())

    Feeds(
        feeds.value,
        vm::onAddFeed,
        vm::onDeleteFeed,
        onUpClick = { navController.popBackStack() },
    )
}

package io.github.rsookram.rss.feeds

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.rsookram.rss.data.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val feeds = repository.feeds()

    fun onAddFeed(url: String) {
        viewModelScope.launch {
            repository.addFeed(url)
        }
    }
}

@Composable
fun FeedsScreen(navController: NavController, vm: FeedsViewModel = hiltViewModel()) {
    val feeds = vm.feeds.collectAsState(emptyList())

    Feeds(
        feeds.value,
        vm::onAddFeed,
        onUpClick = { navController.popBackStack() },
    )
}

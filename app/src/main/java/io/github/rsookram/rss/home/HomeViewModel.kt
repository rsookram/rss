package io.github.rsookram.rss.home

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.rsookram.rss.data.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val items = Pager(
        PagingConfig(pageSize = 30, enablePlaceholders = true),
        pagingSourceFactory = repository::items
    ).flow

    fun onRefresh() {
        viewModelScope.launch {
            repository.sync()
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, vm: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current

    val items = vm.items.collectAsLazyPagingItems()

    Home(
        // TODO: Get state from repo
        isRefreshing = false,
        onRefresh = vm::onRefresh,
        onManageFeedsClick = { navController.navigate("feeds") },
        items = items,
        onItemClick = { item ->
            context.startActivity(Intent(Intent.ACTION_VIEW, item.url.toUri()))
        }
    )
}

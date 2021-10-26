package io.github.rsookram.rss.feeds

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import io.github.rsookram.rss.Feed
import io.github.rsookram.rss.R

@Composable
fun Feeds(feeds: List<Feed>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feeds)) },
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars,
                    applyBottom = false,
                ),
            )
        }
    ) {
        LazyColumn(
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.navigationBars,
            )
        ) {
            // TODO: Render feeds
        }
    }
}

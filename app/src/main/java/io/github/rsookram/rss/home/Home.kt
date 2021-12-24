package io.github.rsookram.rss.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import io.github.rsookram.rss.Item
import io.github.rsookram.rss.R
import io.github.rsookram.rss.ui.OverflowMenu

/**
 * The main screen of the app. Displays an aggregated view of all RSS feeds that have been
 * subscribed to.
 */
@Composable
fun Home(
    onManageFeedsClick: () -> Unit,
    items: LazyPagingItems<Item>,
    onItemClick: (Item) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars,
                    applyBottom = false,
                ),
                actions = {
                    val expanded = rememberSaveable { mutableStateOf(false) }

                    OverflowMenu(expanded) {
                        DropdownMenuItem(
                            onClick = {
                                expanded.value = false
                                onManageFeedsClick()
                            }
                        ) { Text(stringResource(R.string.manage_feeds)) }
                    }
                }
            )
        }
    ) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.navigationBars,
                )
            ) {
                items(items) { item ->
                    if (item != null) {
                        ItemRow(
                            Modifier
                                .clickable { onItemClick(item) }
                                .fillMaxWidth(),
                            item,
                        )
                    } else {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemRow(modifier: Modifier = Modifier, item: Item) {
    Column(
        modifier
            .heightIn(min = 48.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(item.title, style = MaterialTheme.typography.body1)

        Text(item.feedName, Modifier.padding(top = 4.dp), style = MaterialTheme.typography.caption)
    }
}

@Preview
@Composable
private fun ItemRowPreview() {
    Column {
        ItemRow(
            item = Item(
                id = 2,
                url = "https://rsookram.github.io/2021/08/24/testing-composables-with-robolectric.html",
                title = "Testing Composables with Robolectric",
                feedName = "Rashad Sookram",
            )
        )

        ItemRow(
            item = Item(
                id = 1,
                url = "https://rsookram.github.io/2021/08/09/why-i-made-a-new-srs.html",
                title = "Why I Made a New SRS",
                feedName = "Rashad Sookram",
            )
        )
    }
}

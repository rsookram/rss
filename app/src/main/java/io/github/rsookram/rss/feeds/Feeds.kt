package io.github.rsookram.rss.feeds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import io.github.rsookram.rss.Feed
import io.github.rsookram.rss.R

@Composable
fun Feeds(
    feeds: List<Feed>,
    onAddFeed: (String) -> Unit,
    onDeleteFeed: (Feed) -> Unit,
    onUpClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feeds)) },
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars,
                    applyBottom = false,
                ),
                navigationIcon = {
                    IconButton(onClick = onUpClick) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.toolbar_up_description),
                        )
                    }
                },
            )
        }
    ) {
        var showAddFeedDialog by rememberSaveable { mutableStateOf(false) }
        // TODO: Try dialog destination with nav component
        var showDeleteFeedDialog by rememberSaveable { mutableStateOf<Feed?>(null) }

        LazyColumn(
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.navigationBars,
            )
        ) {
            item {
                AddFeedButton(Modifier.clickable { showAddFeedDialog = true })
            }

            items(feeds) { feed ->
                FeedRow(
                    Modifier
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { showDeleteFeedDialog = feed },
                            onLongClickLabel = "",
                        )
                        .fillMaxWidth(),
                    feed = feed,
                )
            }
        }

        if (showAddFeedDialog) {
            AddFeedDialog(
                onConfirm = {
                    onAddFeed(it)
                    showAddFeedDialog = false
                },
                onDismiss = { showAddFeedDialog = false }
            )
        }

        showDeleteFeedDialog?.let { feed ->
            DeleteFeedDialog(
                feed,
                onConfirm = {
                    onDeleteFeed(feed)
                    showDeleteFeedDialog = null
                },
                onDismiss = { showDeleteFeedDialog = null },
            )
        }
    }
}

@Composable
private fun AddFeedButton(modifier: Modifier = Modifier) {
    Row(
        modifier.heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.padding(start = 16.dp),
        )

        Text(
            stringResource(R.string.add_feed),
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun AddFeedDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            var feedUrl by rememberSaveable { mutableStateOf("") }

            Column(Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.add_feed),
                    style = MaterialTheme.typography.h6,
                )

                OutlinedTextField(
                    value = feedUrl,
                    onValueChange = { feedUrl = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = { Text(stringResource(R.string.feed_url)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    singleLine = true,
                )

                Row(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))

                    TextButton(onClick = onDismiss) {
                        Text(stringResource(android.R.string.cancel))
                    }

                    TextButton(onClick = { onConfirm(feedUrl) }) {
                        Text(stringResource(R.string.add_feed_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteFeedDialog(feed: Feed, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete_feed_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        title = {
            Text(stringResource(R.string.delete_feed_title, feed.name))
        },
        text = { Text(feed.url) },
    )
}

@Composable
private fun FeedRow(modifier: Modifier = Modifier, feed: Feed) {
    Column(
        modifier
            .heightIn(min = 48.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(feed.name, style = MaterialTheme.typography.body1)

        Text(
            feed.url,
            Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.caption
        )
    }
}

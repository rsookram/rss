package io.github.rsookram.rss.data

import io.github.rsookram.rss.Database
import io.github.rsookram.rss.Feed
import io.github.rsookram.rss.ItemQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.toptas.rssconverter.RssItem
import javax.inject.Inject

class Repository @Inject constructor(
    private val database: Database,
    private val service: RssService,
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun sync() {
        val feeds = withContext(ioDispatcher) {
            database.feedQueries.feed().executeAsList()
        }

        // TODO: Parallelize
        feeds.forEach { feed ->
            refreshFeed(feed)
        }
    }

    private suspend fun refreshFeed(feed: Feed) {
        val items = service.feed(feed.url).items ?: return

        database.itemQueries.insertAll(feed, items)
    }

    private suspend fun ItemQueries.insertAll(feed: Feed, items: List<RssItem>) {
        withContext(ioDispatcher) {
            transaction {
                items.forEach { item ->
                    val url = item.link
                    val title = item.title
                    val timestamp = item.publishDate

                    // TODO: Don't add items that are too old (> 90 days)
                    if (url != null && title != null && timestamp != null) {
                        insert(feed.id, url, title, timestamp)
                    }
                }
            }
        }
    }
}

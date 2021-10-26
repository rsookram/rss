package io.github.rsookram.rss.data

import androidx.paging.PagingSource
import com.squareup.sqldelight.android.paging3.QueryPagingSource
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.github.rsookram.rss.Database
import io.github.rsookram.rss.Feed
import io.github.rsookram.rss.Item
import io.github.rsookram.rss.ItemQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.toptas.rssconverter.RssItem
import javax.inject.Inject

class Repository @Inject constructor(
    private val database: Database,
    private val service: RssService,
    private val ioDispatcher: CoroutineDispatcher,
) {

    fun items(): PagingSource<Long, Item> {
        val itemQueries = database.itemQueries

        return QueryPagingSource(
            countQuery = itemQueries.countItems(),
            transacter = itemQueries,
            dispatcher = ioDispatcher,
            queryProvider = { limit, offset -> itemQueries.item(limit, offset) },
        )
    }

    fun feeds(): Flow<List<Feed>> =
        database.feedQueries.feed().asFlow().mapToList()

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

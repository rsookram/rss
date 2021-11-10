package io.github.rsookram.rss.data

import androidx.paging.PagingSource
import com.squareup.sqldelight.android.paging3.QueryPagingSource
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.github.rsookram.rss.Database
import io.github.rsookram.rss.Feed
import io.github.rsookram.rss.Item
import io.github.rsookram.rss.ItemQueries
import io.github.rsookram.rss.data.parser.RssFeed
import io.github.rsookram.rss.data.parser.RssItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.Duration
import javax.inject.Inject

class Repository @Inject constructor(
    private val database: Database,
    private val service: RssService,
    private val clock: Clock,
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

    suspend fun addFeed(url: String): Boolean = withContext(ioDispatcher) {
        database.feedQueries.insert(url, name = "")

        val id = database.feedQueries.getLastCreatedId().executeAsOne()
        val (name, items) = refreshFeed(url) ?: return@withContext false

        database.feedQueries.updateName(name, id)
        database.transaction {
            database.itemQueries.insertAll(id, items)
        }

        true
    }

    suspend fun removeFeed(id: Long) = withContext(ioDispatcher) {
        database.feedQueries.delete(id)
    }

    suspend fun sync(): Boolean {
        val feeds = withContext(ioDispatcher) {
            database.feedQueries.feedToSync().executeAsList()
        }

        val rssFeeds = coroutineScope {
            feeds
                .map { (id, url) ->
                    async { id to refreshFeed(url) }
                }
                .awaitAll()
                .mapNotNull { (id, feed) -> if (feed != null) id to feed else null }
        }

        if (rssFeeds.isEmpty()) return false

        withContext(ioDispatcher) {
            database.transaction {
                rssFeeds.forEach { (id, feed) ->
                    database.feedQueries.updateName(feed.name, id)
                    database.itemQueries.insertAll(id, feed.items)
                }
            }
        }

        return true
    }

    private suspend fun refreshFeed(url: String): RssFeed? {
        val (name, items) = try {
            service.feed(url)
        } catch (e: Exception) {
            return null
        }

        // Don't add items that are too old
        val threshold = clock.instant().minus(Duration.ofDays(90))
        val recentItems = items.filter { it.timestamp > threshold }

        return RssFeed(name, recentItems)
    }

    private fun ItemQueries.insertAll(id: Long, items: List<RssItem>) {
        items.forEach { (url, title, timestamp) ->
            insert(id, url, title, timestamp.toString())
        }
    }

    suspend fun prune() {
        withContext(ioDispatcher) {
            database.itemQueries.prune()
        }
    }
}

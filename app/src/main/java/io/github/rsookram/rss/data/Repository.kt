package io.github.rsookram.rss.data

import androidx.paging.PagingSource
import com.squareup.sqldelight.android.paging3.QueryPagingSource
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.github.rsookram.rss.Database
import io.github.rsookram.rss.Feed
import io.github.rsookram.rss.Item
import io.github.rsookram.rss.ItemQueries
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

    suspend fun addFeed(url: String) = withContext(ioDispatcher) {
        database.feedQueries.insert(url, name = "")

        val id = database.feedQueries.getLastCreatedId().executeAsOne()
        refreshFeed(id, url)
    }

    suspend fun sync() {
        val feeds = withContext(ioDispatcher) {
            database.feedQueries.feed().executeAsList()
        }

        coroutineScope {
            feeds
                .map { (id, url) ->
                    async { refreshFeed(id, url) }
                }
                .awaitAll()
        }
    }

    private suspend fun refreshFeed(id: Long, url: String) {
        val (name, items) = service.feed(url)

        // Don't add items that are too old (> 90 days)
        val threshold = clock.instant().minus(Duration.ofDays(90))
        val recentItems = items.filter { it.timestamp > threshold }

        withContext(ioDispatcher) {
            database.feedQueries.updateName(name, id)
            database.itemQueries.insertAll(id, recentItems)
        }
    }

    private fun ItemQueries.insertAll(id: Long, items: List<RssItem>) {
        transaction {
            items.forEach { (url, title, timestamp) ->
                insert(id, url, title, timestamp.toString())
            }
        }
    }

    suspend fun prune() {
        withContext(ioDispatcher) {
            database.itemQueries.prune()
        }
    }
}

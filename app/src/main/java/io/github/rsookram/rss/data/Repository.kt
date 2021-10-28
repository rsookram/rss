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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
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

    suspend fun addFeed(url: String) = withContext(ioDispatcher) {
        database.feedQueries.insert(url, name = "")

        val id = database.feedQueries.getLastCreatedId().executeAsOne()
        refreshFeed(id, url)
    }

    suspend fun sync() {
        val feeds = withContext(ioDispatcher) {
            database.feedQueries.feed().executeAsList()
        }

        // TODO: Parallelize
        feeds.forEach { (id, url) ->
            refreshFeed(id, url)
        }
    }

    private suspend fun refreshFeed(id: Long, url: String) {
        val (name, items) = service.feed(url)

        withContext(ioDispatcher) {
            database.feedQueries.updateName(name, id)
            database.itemQueries.insertAll(id, items)
        }
    }

    private fun ItemQueries.insertAll(id: Long, items: List<RssItem>) {
        transaction {
            items.forEach { (url, title, timestamp) ->
                // TODO: Don't add items that are too old (> 90 days)
                insert(id, url, title, timestamp)
            }
        }
    }
}

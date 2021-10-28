package io.github.rsookram.rss.data

import io.github.rsookram.rss.data.parser.RssFeed
import retrofit2.http.GET
import retrofit2.http.Url

interface RssService {

    @GET suspend fun feed(@Url url: String): RssFeed
}

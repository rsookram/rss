package io.github.rsookram.rss.data.parser

import okhttp3.ResponseBody
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

/**
 * [Converter.Factory] to allow Retrofit interfaces to return [RssFeed]s.
 */
class RssConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? =
        if (type == RssFeed::class.java) {
            RssResponseBodyConverter()
        } else {
            null
        }
}

private class RssResponseBodyConverter : Converter<ResponseBody, RssFeed> {

    override fun convert(value: ResponseBody): RssFeed? {
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

        val document = documentBuilder.parse(value.byteStream())

        val name = document.getElementsByTagName("title").item(0)?.textContent ?: return null

        val entryNodes = document.getElementsByTagName("entry")
        val items = if (entryNodes.length > 0) {
            parseItems(entryNodes, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } else {
            val itemNodes = document.getElementsByTagName("item")
            parseItems(itemNodes, DateTimeFormatter.RFC_1123_DATE_TIME)
        }

        return RssFeed(name, items)
    }
}

private fun parseItems(nodes: NodeList, dateTimeFormatter: DateTimeFormatter): List<RssItem> =
    (0 until nodes.length)
        .map(nodes::item)
        .map { node -> parseItem(node, dateTimeFormatter) }

private fun parseItem(node: Node, dateTimeFormatter: DateTimeFormatter): RssItem {
    var url = ""
    var title = ""
    var timestamp = ""

    for (i in 0 until node.childNodes.length) {
        val child = node.childNodes.item(i)

        when (child.nodeName) {
            "link" -> {
                url = child.textContent.ifEmpty {
                    (child as? Element)?.getAttribute("href").orEmpty()
                }
            }
            "title" -> {
                title = child.textContent
            }
            "pubDate", "updated" -> {
                timestamp = child.textContent
            }
        }
    }

    val instant = dateTimeFormatter.parse(timestamp, Instant::from)
    return RssItem(url, title, instant)
}

/**
 * The data for a single RSS feed.
 */
data class RssFeed(val name: String, val items: List<RssItem>)

/**
 * A single entry within an RSS feed.
 */
data class RssItem(val url: String, val title: String, val timestamp: Instant)

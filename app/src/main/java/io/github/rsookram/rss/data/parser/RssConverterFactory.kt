package io.github.rsookram.rss.data.parser

import okhttp3.ResponseBody
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.xml.parsers.DocumentBuilderFactory

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

        // TODO: Handle RSS. This only handles atom now
        val name = document.getElementsByTagName("title").item(0)?.textContent ?: return null
        val entryNodes = document.getElementsByTagName("entry") ?: return null

        return RssFeed(
            name,
            entryNodes.toItems(),
        )
    }
}

private fun NodeList.toItems(): List<RssItem> =
    (0 until length)
        .map(this::item)
        .map(Node::toItem)

private fun Node.toItem(): RssItem {
    var url = ""
    var title = ""
    var timestamp = ""

    for (i in 0 until childNodes.length) {
        val node = childNodes.item(i)

        when (node.nodeName) {
            "link" -> {
                url = node.attributes.getNamedItem("href").textContent
            }
            "title" -> {
                title = node.textContent
            }
            "updated" -> {
                timestamp = node.textContent
            }
        }
    }

    // TODO: Parse timestamp
    return RssItem(url, title, timestamp)
}

data class RssFeed(val name: String, val items: List<RssItem>)

data class RssItem(val url: String, val title: String, val timestamp: String)

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

        val name = document.getElementsByTagName("title").item(0)?.textContent ?: return null

        val isAtom = document.firstChild.nodeName == "feed"
        val items = if (isAtom) {
            parseAtom(document.getElementsByTagName("entry"))
        } else {
            parseRss(document.getElementsByTagName("item"))
        }

        return RssFeed(name, items)
    }
}

// TODO: Deduplicate code
private fun parseAtom(nodes: NodeList): List<RssItem> =
    (0 until nodes.length)
        .map(nodes::item)
        .map(::parseAtomItem)

private fun parseAtomItem(node: Node): RssItem {
    var url = ""
    var title = ""
    var timestamp = ""

    for (i in 0 until node.childNodes.length) {
        val child = node.childNodes.item(i)

        when (child.nodeName) {
            "link" -> {
                url = child.attributes.getNamedItem("href").textContent
            }
            "title" -> {
                title = child.textContent
            }
            "updated" -> {
                timestamp = child.textContent
            }
        }
    }

    // TODO: Parse timestamp
    return RssItem(url, title, timestamp)
}

private fun parseRss(nodes: NodeList): List<RssItem> =
    (0 until nodes.length)
        .map(nodes::item)
        .map(::parseRssItem)

private fun parseRssItem(node: Node): RssItem {
    var url = ""
    var title = ""
    var timestamp = ""

    for (i in 0 until node.childNodes.length) {
        val child = node.childNodes.item(i)

        when (child.nodeName) {
            "link" -> {
                url = child.textContent
            }
            "title" -> {
                title = child.textContent
            }
            "pubDate" -> {
                timestamp = child.textContent
            }
        }
    }

    // TODO: Parse timestamp
    return RssItem(url, title, timestamp)
}

data class RssFeed(val name: String, val items: List<RssItem>)

data class RssItem(val url: String, val title: String, val timestamp: String)

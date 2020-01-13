package ru.cscenter.fingerpaint.resources

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.Color
import org.xmlpull.v1.XmlPullParser

class GameResourceParser(private val context: Context, xmlResourceId: Int) {

    private val parser: XmlResourceParser = context.resources.getXml(xmlResourceId)

    companion object {
        private const val COLORS_TAG = "colors"
        private const val COLOR_TAG = "color"
        private const val FIGURES_TAG = "figures"
        private const val FIGURE_TAG = "figure"
        private const val LETTERS_TAG = "letters"
        private const val LETTER_TAG = "letter"
        private const val ID_TAG = "id"
        private const val NAME_TAG = "name"
        private const val DRAWABLE_RESOURCE = "drawable"
    }

    fun parseXML(): GameResources {
        var colors: List<MyColor>? = null
        var figures: List<Figure>? = null
        var letters: List<Letter>? = null
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            when (parser.name) {
                COLORS_TAG -> colors = getColors()
                FIGURES_TAG -> figures = getFigures()
                LETTERS_TAG -> letters = getLetters()
                else -> parser.next()
            }
        }

        checkNotNull(colors) { "$COLORS_TAG tag not specified." }
        checkNotNull(figures) { "$FIGURES_TAG tag not specified." }
        checkNotNull(letters) { "$LETTERS_TAG tag not specified." }
        return GameResources(colors, figures, letters)
    }

    private fun XmlResourceParser.isStart() = eventType == XmlPullParser.START_TAG
    private fun XmlResourceParser.isEnd() = eventType == XmlPullParser.END_TAG

    private fun getColors() = parseList(COLORS_TAG) { getColor() }
    private fun getFigures() = parseList(FIGURES_TAG) { getFigure() }
    private fun getLetters() = parseList(LETTERS_TAG) { getLetter() }

    private fun getColor(): MyColor {
        val (color, name) = parseItem(COLOR_TAG, { getColorId() })
        return MyColor(color, name)
    }

    private fun getFigure(): Figure {
        val (resourceId, name) = parseItem(FIGURE_TAG, { getDrawableId() })
        return Figure(resourceId, name)
    }

    private fun getLetter(): Letter {
        val (resourceId, name) = parseItem(LETTER_TAG, { getDrawableId() })
        return Letter(resourceId, name)
    }

    private fun getColorId() = getTagData(ID_TAG) { x ->
        try {
            return@getTagData Color.parseColor(x)
        } catch (e: Throwable) {
            throw IllegalStateException("Illegal color format.", e)
        }
    }

    private fun getDrawableId(): Int = getTagData(ID_TAG) { name -> getDrawableResourceId(name) }

    private fun getDrawableResourceId(resourceName: String): Int {
        val id = context.resources.getIdentifier(
            resourceName,
            DRAWABLE_RESOURCE,
            context.packageName
        )
        check(id != 0)
        return id
    }

    private fun getName() = getTagData(NAME_TAG) { x -> x }

    private fun <T> parseList(
        tagName: String,
        itemParser: () -> T
    ): List<T> {
        check(parser.isStart() && parser.name == tagName)
        parser.next()

        val result = mutableListOf<T>()
        while (!(parser.isEnd() && parser.name == tagName)) {
            val item = itemParser()
            result.add(item)
        }
        parser.next()
        return result
    }

    private fun parseItem(
        tagName: String,
        idParser: () -> Int,
        nameParser: () -> String = { getName() }
    ): Pair<Int, String> {
        check(parser.isStart() && parser.name == tagName)
        parser.next()
        var id: Int? = null
        var name: String? = null

        while (!(parser.isEnd() && parser.name == tagName)) {
            check(parser.isStart())
            when (parser.name) {
                ID_TAG -> {
                    check(id == null) { "$ID_TAG tag specified twice for $tagName." }
                    id = idParser()
                }
                NAME_TAG -> {
                    check(name == null) { "$NAME_TAG tag specified twice for $tagName." }
                    name = nameParser()
                }
                else -> error("Unexpected tag ${parser.name}")
            }
        }
        parser.next()
        checkNotNull(id) { "$ID_TAG tag not specified for $tagName." }
        checkNotNull(name) { "$NAME_TAG tag not specified for $tagName." }
        return Pair(id, name)
    }

    private fun <T> getTagData(
        tagName: String,
        mapper: (String) -> T
    ): T {
        check(parser.isStart() && parser.name == tagName)
        parser.next()
        check(parser.eventType == XmlPullParser.TEXT)
        val result = mapper(parser.text)
        parser.next()
        check(parser.isEnd() && parser.name == tagName)
        parser.next()
        return result
    }
}
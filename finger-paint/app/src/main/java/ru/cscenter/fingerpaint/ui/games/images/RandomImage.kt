package ru.cscenter.fingerpaint.ui.games.images

import ru.cscenter.fingerpaint.ui.Colors
import java.util.*

private val random = Random()

private val letters = ('А'..'Я')
    .plusElement('Ё')
    .map { char -> char.toString() }
    .toList()


val figuresRandom = ListRandom(FigureType.values().toList())
val lettersRandom = ListRandom(letters)
val colorsRandom = ListRandom(Colors.colors)


class ListRandom<T>(private val values: List<T>) {
    private val count = values.size

    private fun getRandomValue(index: Int): T = values[index]
    private fun getRandomIndex() = random.nextInt(count)
    fun getRandomValue() = getRandomValue(getRandomIndex())
    fun getRandomPair(): Pair<T, T> {
        val index1 = getRandomIndex()
        var index2 = getRandomIndex()
        while (index1 == index2) {
            index2 = getRandomIndex()
        }

        return Pair(getRandomValue(index1), getRandomValue(index2))
    }
}

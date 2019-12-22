package ru.cscenter.fingerpaint.ui.games.tasks

import ru.cscenter.fingerpaint.ui.Colors
import ru.cscenter.fingerpaint.ui.games.images.FigureType

private fun figureToString(figure: FigureType) = when (figure) {
    FigureType.CIRCLE -> "круг"
    FigureType.SQUARE -> "квадрат"
    FigureType.RECTANGLE -> "прямоугольник"
    FigureType.TRIANGLE -> "треугольник"
}


private fun colorToString(color: Int) = when (color) {
    Colors.blue -> "синюю"
    Colors.green -> "зеленую"
    Colors.pink -> "розововую"
    Colors.purple -> "фиолетовую"
    Colors.red -> "красную"
    Colors.yellow -> "желтую"
    else -> ""
}

fun getChooseFigureTask(figure: FigureType) = "Выбери ${figureToString(figure)}"
fun getChooseFigureColorTask(color: Int) = "Выбери ${colorToString(color)} фигуру"
fun getDrawFigureTask(figure: FigureType) = "Раскрась ${figureToString(figure)}"
fun getChooseLetterTask(letter: String) = "Выбери букву $letter"
fun getChooseLetterColorTask(color: Int) = "Выбери ${colorToString(color)} букву"
fun getDrawLetterTask() = "Закрась букву"
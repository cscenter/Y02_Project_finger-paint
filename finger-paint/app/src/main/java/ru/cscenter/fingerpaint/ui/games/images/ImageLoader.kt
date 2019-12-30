package ru.cscenter.fingerpaint.ui.games.images

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.min

private const val FIGURE_COMPRESSION_RATE = 8
private const val LETTER_COMPRESSION_RATE = 8
private const val STROKE_WIDTH_RATE = 1 / 50f

private fun getStrokeWidth(
    imageWidth: Int,
    imageHeight: Int, compressionRate: Int
) = (min(imageWidth, imageHeight) / compressionRate) * STROKE_WIDTH_RATE

fun getFigureImage(
    type: FigureType,
    color: Int = Color.BLACK,
    isFilled: Boolean = true
) = getFigureImageCompressed(type, color, isFilled, 1)

fun getFigureImageCompressed(
    type: FigureType,
    color: Int = Color.BLACK,
    isFilled: Boolean = true,
    compressionRate: Int = FIGURE_COMPRESSION_RATE
): (Int, Int) -> Bitmap = { width, height ->
    Images.getFigureImageBitmap(
        type,
        width / compressionRate,
        height / compressionRate,
        getStrokeWidth(width, height, compressionRate),
        isFilled,
        color
    )
}

fun getLetterImage(
    text: String,
    color: Int = Color.BLACK
) = getLetterImageCompressed(text, color, 1)

fun getLetterImageCompressed(
    text: String,
    color: Int = Color.BLACK,
    compressionRate: Int = LETTER_COMPRESSION_RATE
): (Int, Int) -> Bitmap = { width, height ->
    Images.getTextImageBitmap(
        text,
        width / compressionRate,
        height / compressionRate,
        color
    )
}

package ru.cscenter.fingerpaint.ui.games

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import kotlin.math.min

enum class FigureType {
    CIRCLE,
    SQUARE,
    RECTANGLE
}

object Images {
    fun getTextImageBitmap(
        text: String,
        width: Int,
        height: Int,
        textSize: Float = min(width, height).toFloat()
    ): Bitmap {
        val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        val textPaint = Paint().apply {
            isDither = true
            color = Color.BLACK
            textAlign = Align.CENTER
            this.textSize = textSize
        }
        val xPos = canvas.width / 2
        val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2).toInt()

        canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), textPaint)
        return bm
    }

    fun getFigureImageBitmap(
        figureType: FigureType,
        width: Int,
        height: Int,
        strokeWidth: Float,
        isFilled: Boolean = false
    ): Bitmap {
        val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        val paint = Paint().apply {
            isDither = true
            color = Color.BLACK
            if (isFilled) {
                style = Paint.Style.FILL
            } else {
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
                this.strokeWidth = strokeWidth
            }
        }
        when (figureType) {
            FigureType.CIRCLE -> canvas.drawCircle(
                width / 2f,
                height / 2f,
                min(width, height) / 2.1f,
                paint
            )

            FigureType.SQUARE -> {
                val side = min(width, height).toFloat()
                canvas.drawRect(side / 10, side / 10, 9 * side / 10, 9 * side / 10, paint)
            }

            FigureType.RECTANGLE -> {
                canvas.drawRect(width / 10f, height / 4f, 9 * width / 10f, 3 * height / 4f, paint)
            }
        }
        return bm
    }
}
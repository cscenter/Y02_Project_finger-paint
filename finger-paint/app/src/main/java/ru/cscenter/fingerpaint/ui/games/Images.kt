package ru.cscenter.fingerpaint.ui.games

import android.graphics.*
import android.graphics.Paint.Align
import android.os.Build
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.RequiresApi
import kotlin.math.min

enum class FigureType {
    CIRCLE,
    SQUARE,
    RECTANGLE
}

fun setImageAsSoonAsPossible(view: ImageView, imageSupplier: (width: Int, height: Int) -> Bitmap) {
    val viewTreeObserver = view.viewTreeObserver
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                view.setImageBitmap(
                    imageSupplier(
                        view.width,
                        view.height
                    )
                )
            }
        })
    }
}

object Images {

    /**
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param paint
     * the Paint to set the text size for
     * @param desiredWidth
     * the desired width
     * @param text
     * the text that should be that width
     */
    private fun setTextSizeForPaint(
        text: String,
        paint: Paint,
        desiredWidth: Float,
        desiredHeight: Float
    ) {
        var testTextSize = 1f
        // Get the bounds of the text, using our testTextSize.
        val bounds = Rect()
        do {
            testTextSize++
            paint.textSize = testTextSize
            paint.getTextBounds(text, 0, text.length, bounds)
        } while (bounds.width() < desiredWidth && bounds.height() < desiredHeight)
        paint.textSize = testTextSize - 1
    }

    fun getTextImageBitmap(
        text: String,
        width: Int,
        height: Int,
        textColor: Int = Color.BLACK
    ): Bitmap {
        val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        val textPaint = Paint().apply {
            isDither = true
            color = textColor
            textAlign = Align.CENTER
        }
        setTextSizeForPaint(text, textPaint, width.toFloat(), height.toFloat())
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
        isFilled: Boolean = false,
        figureColor: Int = Color.BLACK
    ): Bitmap {
        val paint = Paint().apply {
            isDither = true
            color = figureColor
            style = if (isFilled) {
                Paint.Style.FILL_AND_STROKE
            } else {
                Paint.Style.STROKE
            }
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            this.strokeWidth = strokeWidth
        }
        val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        when (figureType) {
            // TODO fix magic constants
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
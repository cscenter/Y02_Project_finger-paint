package ru.cscenter.fingerpaint.ui.games.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ProgressBar
import com.divyanshu.draw.widget.DrawView
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.service.MyVibrator
import java.util.*
import kotlin.math.roundToInt

@SuppressLint("ClickableViewAccessibility")
class DrawChecker(
    private val context: Context,
    private val drawView: DrawView,
    private val image: Bitmap,
    private val thresholds: Pair<Float, Float>,
    private val progressBars: Pair<ProgressBar, ProgressBar>,
    private val callback: (GameResult) -> Unit
) {

    private val widthScale = image.width.toFloat() / drawView.width
    private val heightScale = image.height.toFloat() / drawView.height

    private val blackPixels: List<Pair<Int, Int>>
    private val whitePixels: List<Pair<Int, Int>>
    private var bitmap: Bitmap? = null
    private var cancelled = false
    private var lastUpdated = 0L

    init {
        val pixels = calculateGoodBadPixels()
        blackPixels = pixels.first
        whitePixels = pixels.second
        progressBars.first.max = (blackPixels.size * thresholds.first + 1).toInt()
        progressBars.second.max = (whitePixels.size * thresholds.second + 1).toInt()

        drawView.setOnTouchListener { _, event ->
            if (lastUpdated + UPDATE_PERIOD_MS < System.currentTimeMillis()) {
                lastUpdated = System.currentTimeMillis()
                updateProgress()
            }
            drawView.onTouchEvent(event)
        }
    }

    fun cancel() {
        cancelled = true
    }

    private fun getPixelColor(x: Int, y: Int): Int {
        return bitmap!!.getPixel((x / widthScale).roundToInt(), (y / heightScale).roundToInt())
    }

    fun updateProgress() {
        if (cancelled) return
        bitmap?.recycle()
        bitmap = drawView.getBitmap()

        val countBlack = blackPixels.count { getPixelColor(it.first, it.second) in colors }
        val countWhite = whitePixels.count { getPixelColor(it.first, it.second) in colors }

        val (goodProgress, badProgress) = progressBars
        val (thresholdSuccess, thresholdFail) = thresholds

        goodProgress.progress = countBlack
        if (badProgress.progress < countWhite && MainApplication.settings.getVibrate()) {
            MyVibrator.vibrate(context, MyVibrator.LENGTH_SHORT)
        }
        badProgress.progress = countWhite

        if (countWhite.toFloat() / whitePixels.size >= thresholdFail) {
            callback(GameResult.FAIL)
            cancel()
        } else if (countBlack.toFloat() / blackPixels.size >= thresholdSuccess) {
            callback(GameResult.SUCCESS)
            cancel()
        }
    }

    private fun calculateGoodBadPixels(): Pair<List<Pair<Int, Int>>, List<Pair<Int, Int>>> {
        val blackPixels = mutableListOf<Pair<Int, Int>>()
        val whitePixels = mutableListOf<Pair<Int, Int>>()
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val pixel = image.getPixel(x, y)
                if (pixel == Color.BLACK) {
                    blackPixels.add(Pair(x, y))
                } else {
                    whitePixels.add(Pair(x, y))
                }
            }
        }

        val randomGenerator = Random(42)
        return Pair(
            blackPixels.shuffled(randomGenerator).take(RANDOM_BLACK_PIXELS_COUNT),
            whitePixels.shuffled(randomGenerator).take(RANDOM_WHITE_PIXELS_COUNT)
        )
    }


    companion object {
        private const val UPDATE_PERIOD_MS = 100L
        private const val RANDOM_BLACK_PIXELS_COUNT = 3000 // depends on cpu speed
        private const val RANDOM_WHITE_PIXELS_COUNT = 3000
        val colors = MainApplication.gameResources.colors.map { it.color }.toHashSet()
    }
}

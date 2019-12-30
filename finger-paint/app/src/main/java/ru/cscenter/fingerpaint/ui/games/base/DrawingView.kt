package ru.cscenter.fingerpaint.ui.games.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt


@SuppressLint("ViewConstructor")
class DrawingView(
    c: Context?,
    private val imageSupplier: (width: Int, height: Int) -> Bitmap,
    private val backgroundImageSupplier: (width: Int, height: Int) -> Bitmap,
    private val thresholds: Pair<Float, Float>,
    private val paintColor: Int,
    private val progressBars: Pair<ProgressBar, ProgressBar>,
    private val callback: BaseGameCallback
) : AppCompatImageView(c) {
    private lateinit var mBitmap: Bitmap // TRANSPARENT, user draw on it
    private var mCanvas: Canvas? = null
    private lateinit var bitmap: Bitmap // initial, black-white

    private val mPath: Path = Path()
    private val circlePath: Path = Path()

    private var scaleRate: Float = 1f
    private var bitmapScaleRate: Float = 1f

    private var blackPixels = mutableListOf<Pair<Int, Int>>()
    private var whitePixels = mutableListOf<Pair<Int, Int>>()

    private var mX = 0f
    private var mY = 0f

    private val touchPxTolerance = fromDpToPx(TOUCH_TOLERANCE_DP)
    private val paintStrokeWidth = fromDpToPx(PAINT_STROKE_WIDTH_DP)
    private val circlePaintStrokeWidth = fromDpToPx(CIRCLE_PAINT_STROKE_WIDTH_DP)
    private val circlePaintRadius = fromDpToPx(CIRCLE_PAINT_RADIUS_DP)

    private val mBitmapPaint: Paint = Paint(Paint.DITHER_FLAG)
    private val mPaint: Paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = paintColor
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = paintStrokeWidth
    }
    private val circlePaint: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.MITER
        strokeWidth = circlePaintStrokeWidth
    }

    private fun fromDpToPx(dp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

    private fun calculateGoodBadPixels() {
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                if (pixel == Color.BLACK) {
                    blackPixels.add(Pair(x, y))
                } else {
                    whitePixels.add(Pair(x, y))
                }
            }
        }
        Log.d("FingerPaint", "Found ${blackPixels.size} black pixels and ${whitePixels.size} other")

        val randomGenerator = Random(42)
        blackPixels =
            blackPixels.shuffled(randomGenerator).take(RANDOM_BLACK_PIXELS_COUNT).toMutableList()
        whitePixels =
            whitePixels.shuffled(randomGenerator).take(RANDOM_WHITE_PIXELS_COUNT).toMutableList()

        progressBars.first.max = (blackPixels.size * thresholds.first + 1).toInt()
        progressBars.second.max = (whitePixels.size * thresholds.second + 1).toInt()
    }

    private fun updateProgress(bm: Bitmap) {
        val countBlack = blackPixels.count {
            bm.getPixel(
                (it.first * bitmapScaleRate).roundToInt(),
                (it.second * bitmapScaleRate).roundToInt()
            ) != Color.TRANSPARENT
        }

        val countWhite = whitePixels.count {
            bm.getPixel(
                (it.first * bitmapScaleRate).roundToInt(),
                (it.second * bitmapScaleRate).roundToInt()
            ) != Color.TRANSPARENT
        }

        val (goodProgress, badProgress) = progressBars
        val (thresholdSuccess, thresholdFail) = thresholds

        goodProgress.progress = countBlack
        badProgress.progress = countWhite

        Log.d("FingerPaint", "Black(good): $countBlack / ${blackPixels.size}")
        Log.d("FingerPaint", "White(bad): $countWhite / ${whitePixels.size}")

        if (countWhite.toFloat() / whitePixels.size >= thresholdFail) {
            callback.onResult(GameResult.FAIL)
        } else if (countBlack.toFloat() / blackPixels.size >= thresholdSuccess) {
            callback.onResult(GameResult.SUCCESS)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMeasure = MeasureSpec.getSize(widthMeasureSpec)
        val heightMeasure = MeasureSpec.getSize(heightMeasureSpec)
        setImageBitmap(backgroundImageSupplier(widthMeasure, heightMeasure))
        val d = drawable
        if (d != null) {
            val rate1: Float = widthMeasure.toFloat() / d.intrinsicWidth
            val rate2: Float = heightMeasure.toFloat() / d.intrinsicHeight

            scaleRate = min(rate1, rate2) // min for INSIDE, max for CROP

            val newWidth = (d.intrinsicWidth * scaleRate).roundToInt()
            val newHeight = (d.intrinsicHeight * scaleRate).roundToInt()

            bitmap = imageSupplier(newWidth, newHeight)
            calculateGoodBadPixels()

            bitmapScaleRate = (d.intrinsicWidth.toFloat() / bitmap.width) * scaleRate

            Log.d(
                "FingerPaint",
                "measureSpecSize: ${MeasureSpec.getSize(widthMeasureSpec)} " +
                        "${MeasureSpec.getSize(heightMeasureSpec)}\n" +
                        "intrinsicSize: ${d.intrinsicWidth} ${d.intrinsicHeight}\n" +
                        "initBitmapSize: ${bitmap.width} ${bitmap.height}\n" +
                        "rates: $rate1 $rate2\n" +
                        "scaleRate: $scaleRate\n" +
                        "bitmapScaleRate: $bitmapScaleRate"
            )

            setMeasuredDimension(newWidth, newHeight)
        } else super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
        canvas.drawPath(mPath, mPaint)
        canvas.drawPath(circlePath, circlePaint)
    }

    private fun touchStart(x: Float, y: Float) {
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= touchPxTolerance || dy >= touchPxTolerance) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
            touchUp()
            touchStart(x, y)
            circlePath.reset()
            circlePath.addCircle(mX, mY, circlePaintRadius, Path.Direction.CW)
        }
    }

    private fun touchUp() {
        mPath.lineTo(mX, mY)
        circlePath.reset()
        // commit the path to our offscreen
        mCanvas!!.drawPath(mPath, mPaint)
        // kill this so we don't double draw
        mPath.reset()
        updateProgress(mBitmap)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        return true
    }


    companion object {
        // !!! should be converted to px in constructor
        private const val TOUCH_TOLERANCE_DP = 4f // in dp
        private const val PAINT_STROKE_WIDTH_DP = 25f // in dp
        private const val CIRCLE_PAINT_STROKE_WIDTH_DP = 5f // in dp
        private const val CIRCLE_PAINT_RADIUS_DP = 30f // in dp
        private const val RANDOM_BLACK_PIXELS_COUNT = 3000 // should be depended on cpu speed
        private const val RANDOM_WHITE_PIXELS_COUNT = 3000
    }
}

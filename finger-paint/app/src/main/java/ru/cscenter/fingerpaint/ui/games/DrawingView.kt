package ru.cscenter.fingerpaint.ui.games

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt


class DrawingView : AppCompatImageView {
    private lateinit var mPaint: Paint
    private lateinit var mBitmap: Bitmap
    private var mCanvas: Canvas? = null
    private lateinit var mPath: Path
    private lateinit var mBitmapPaint: Paint
    private lateinit var circlePaint: Paint
    private lateinit var circlePath: Path
    private var scaleRate: Float = 1f
    private var bitmapScaleRate: Float = 1f
    private var blackPixels: ArrayList<Pair<Int, Int>> = ArrayList()
    private var whitePixels: ArrayList<Pair<Int, Int>> = ArrayList()
    private lateinit var bitmap: Bitmap
    private lateinit var thresholds: Pair<Float, Float>
    private lateinit var progressBars: Pair<ProgressBar, ProgressBar>
    private lateinit var callback: GameActivity.GameCallback
    // constants, assigned in contructor
    private var TOUCH_TOLERANCE_PX = 0f
    private var PAINT_STROKE_WIDTH_PX = 0f
    private var CIRCLE_PAINT_STROKE_WIDTH_PX = 0f
    private var CIRCLE_PAINT_RADIUS_PX = 0f

    // lint requires
    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, magic: Int) : super(c, attrs, magic)

    constructor(
        c: Context?,
        bitmap: Bitmap,
        thresholds: Pair<Float, Float>,
        progressBars: Pair<ProgressBar, ProgressBar>,
        callback: GameActivity.GameCallback
    ) : super(c) {
        this.bitmap = bitmap
        this.thresholds = thresholds
        this.progressBars = progressBars
        this.callback = callback

        // convert constants in pixels to constants in dp
        fun fromDpToPx(dp: Float): Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        TOUCH_TOLERANCE_PX = fromDpToPx(TOUCH_TOLERANCE_DP)
        PAINT_STROKE_WIDTH_PX = fromDpToPx(PAINT_STROKE_WIDTH_DP)
        CIRCLE_PAINT_STROKE_WIDTH_PX = fromDpToPx(CIRCLE_PAINT_STROKE_WIDTH_DP)
        CIRCLE_PAINT_RADIUS_PX = fromDpToPx(CIRCLE_PAINT_RADIUS_DP)

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
        blackPixels.shuffle(Random(42))
        blackPixels =
            ArrayList(blackPixels.take(3000)) // number of random black pixels for progress bar
        whitePixels.shuffle(Random(239))
        whitePixels =
            ArrayList(whitePixels.take(3000)) // number of random (not black) pixels for progress bar

        mPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = Color.YELLOW
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = PAINT_STROKE_WIDTH_PX
        }

        mPath = Path()
        mBitmapPaint = Paint(Paint.DITHER_FLAG)

        circlePath = Path()
        circlePaint = Paint().apply {
            isAntiAlias = true
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.MITER
            strokeWidth = CIRCLE_PAINT_STROKE_WIDTH_PX
        }
    }

    private fun updateProgress(bm: Bitmap) {
        val countBlack = blackPixels.filter {
            bm.getPixel(
                (it.first * bitmapScaleRate).roundToInt(),
                (it.second * bitmapScaleRate).roundToInt()
            ) != Color.TRANSPARENT
        }.size

        val countWhite = whitePixels.filter {
            bm.getPixel(
                (it.first * bitmapScaleRate).roundToInt(),
                (it.second * bitmapScaleRate).roundToInt()
            ) != Color.TRANSPARENT
        }.size

        val goodProgress: ProgressBar = progressBars.first
        val thresholdSuccess = thresholds.first
        val badProgress: ProgressBar = progressBars.second
        val thresholdFail = thresholds.second

        goodProgress.max = blackPixels.size
        goodProgress.progress = countBlack

        badProgress.max = whitePixels.size
        badProgress.progress = countWhite

        Log.d("FingerPaint", "Black(good): $countBlack / ${blackPixels.size}")
        Log.d("FingerPaint", "White(bad): $countWhite / ${whitePixels.size}")

        if (countWhite.toFloat() / whitePixels.size >= thresholdFail) {
            callback.onResult(GameResult.FAIL)
        }

        if (countBlack.toFloat() / blackPixels.size >= thresholdSuccess) {
            callback.onResult(GameResult.SUCCESS)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d = drawable
        if (d != null) {
            val rate1: Float = MeasureSpec.getSize(widthMeasureSpec).toFloat() / d.intrinsicWidth
            val rate2: Float = MeasureSpec.getSize(heightMeasureSpec).toFloat() / d.intrinsicHeight

            scaleRate = min(rate1, rate2) // min for INSIDE, max for CROP
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

            setMeasuredDimension(
                (d.intrinsicWidth * scaleRate).roundToInt(),
                (d.intrinsicHeight * scaleRate).roundToInt()
            )
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

    private var mX = 0f
    private var mY = 0f

    private fun touchStart(x: Float, y: Float) {
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= TOUCH_TOLERANCE_PX || dy >= TOUCH_TOLERANCE_PX) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
            touchUp()
            touchStart(x, y)
            circlePath.reset()
            circlePath.addCircle(mX, mY, CIRCLE_PAINT_RADIUS_PX, Path.Direction.CW)
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
        private const val PAINT_STROKE_WIDTH_DP = 20f // in dp
        private const val CIRCLE_PAINT_STROKE_WIDTH_DP = 5f // in dp
        private const val CIRCLE_PAINT_RADIUS_DP = 30f // in dp
    }
}
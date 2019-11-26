package ru.cscenter.fingerpaint.ui.games

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import ru.cscenter.fingerpaint.R
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
    private var thresholdSuccess: Float = 0f
    private var thresholdFail: Float = 0f
    private lateinit var callback: GameActivity.GameCallback

    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, magic: Int) : super(c, attrs, magic)

    constructor(
        c: Context?,
        bitmap: Bitmap,
        thresholdSuccess: Float,
        thresholdFail: Float,
        callback: GameActivity.GameCallback
    ) : super(c) {
        this.bitmap = bitmap
        this.thresholdSuccess = thresholdSuccess
        this.thresholdFail = thresholdFail
        this.callback = callback
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
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = Color.YELLOW
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = PAINT_STROKE_WIDTH

        mPath = Path()
        mBitmapPaint = Paint(Paint.DITHER_FLAG)
        circlePaint = Paint()
        circlePath = Path()
        circlePaint.isAntiAlias = true
        circlePaint.color = Color.BLUE
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeJoin = Paint.Join.MITER
        circlePaint.strokeWidth = CIRCLE_PAINT_STROKE_WIDTH
    }

    private fun updateProgress(bm: Bitmap) {
        var countBlack = 0
        var countWhite = 0

        for ((x, y) in blackPixels) {
            if (bm.getPixel(
                    (x * bitmapScaleRate).roundToInt(),
                    (y * bitmapScaleRate).roundToInt()
                ) != Color.TRANSPARENT
            ) {
                countBlack++
            }
        }

        for ((x, y) in whitePixels) {
            if (bm.getPixel(
                    (x * bitmapScaleRate).roundToInt(),
                    (y * bitmapScaleRate).roundToInt()
                ) != Color.TRANSPARENT
            ) {
                countWhite++
            }
        }
        val progressLayout: LinearLayout = rootView.findViewById(R.id.drawing_progress_layout)
        val goodProgress: ProgressBar = progressLayout.findViewById(R.id.good_progress)
        val badProgress: ProgressBar = progressLayout.findViewById(R.id.bad_progress)
        goodProgress.max = blackPixels.size
        goodProgress.progress = countBlack

        badProgress.max = whitePixels.size
        badProgress.progress = countWhite

        Log.d("FingerPaint", "Black(good): $countBlack / ${blackPixels.size}")
        Log.d("FingerPaint", "White(bad): $countWhite / ${whitePixels.size}")

        if (countWhite.toFloat() / whitePixels.size + 0.001 >= thresholdFail) {
            callback.onResult(GameResult.FAIL)
        }

        if (countBlack.toFloat() / blackPixels.size + 0.001 >= thresholdSuccess) {
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
            mPaint.strokeWidth = PAINT_STROKE_WIDTH * scaleRate
            circlePaint.strokeWidth = CIRCLE_PAINT_STROKE_WIDTH * scaleRate
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
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
            circlePath.reset()
            circlePath.addCircle(mX, mY, CIRCLE_PAINT_RADIUS * scaleRate, Path.Direction.CW)
            touchUp()
            touchStart(x, y)
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
        private const val TOUCH_TOLERANCE = 4f
        private const val PAINT_STROKE_WIDTH = 25f
        private const val CIRCLE_PAINT_STROKE_WIDTH = 8f
        private const val CIRCLE_PAINT_RADIUS = 30f
    }
}
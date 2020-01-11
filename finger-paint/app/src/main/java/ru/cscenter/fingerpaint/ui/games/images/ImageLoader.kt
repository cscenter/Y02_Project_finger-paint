package ru.cscenter.fingerpaint.ui.games.images

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.get
import ru.cscenter.fingerpaint.ui.games.base.DrawingView
import java.util.*

private const val GET_IMAGE_DEFAULT_COMPRESSION_RATE = 1
private const val GET_IMAGE_COMPRESSED_DEFAULT_COMPRESSION_RATE = 8

fun getImage(
    resourceId: Int,
    resources: Resources,
    color: Int = Color.BLACK
): (Int, Int) -> Bitmap =
    getImageCompressed(resourceId, resources, color, GET_IMAGE_DEFAULT_COMPRESSION_RATE)

fun getImageCompressed(
    resourceId: Int,
    resources: Resources,
    color: Int = Color.BLACK, // important for black/white(good/bad) pixels
    compressionRate: Int = GET_IMAGE_COMPRESSED_DEFAULT_COMPRESSION_RATE
): (Int, Int) -> Bitmap = { width, height ->
    val bitmap = getBitmapFromResource(
        width / compressionRate,
        height / compressionRate,
        resources,
        resourceId
    )

    for (y in 0 until bitmap.height) {
        for (x in 0 until bitmap.width) {
            if (Color.alpha(bitmap.getPixel(x, y)) != 0) {
                bitmap.setPixel(x, y, color)
            }
        }
    }

    bitmap
}

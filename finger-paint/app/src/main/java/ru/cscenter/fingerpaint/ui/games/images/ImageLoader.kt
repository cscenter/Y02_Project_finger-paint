package ru.cscenter.fingerpaint.ui.games.images

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color

private const val COMPRESSION_RATE = 8

fun getImage(
    resourceId: Int,
    resources: Resources,
    color: Int = Color.BLACK
): (Int, Int) -> Bitmap = { width, height ->
    getBitmapFromResource(width, height, resources, resourceId)
}

fun getImageCompressed(
    resourceId: Int,
    resources: Resources,
    color: Int = Color.BLACK,
    compressionRate: Int = COMPRESSION_RATE
): (Int, Int) -> Bitmap = { width, height ->
    getBitmapFromResource(
        width / compressionRate,
        height / compressionRate,
        resources,
        resourceId
    )
}

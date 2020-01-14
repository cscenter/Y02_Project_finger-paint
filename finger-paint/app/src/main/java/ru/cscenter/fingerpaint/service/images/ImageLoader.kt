package ru.cscenter.fingerpaint.service.images

import android.content.res.Resources
import android.graphics.*


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
    val sourceImage = getBitmapFromResource(
        width / compressionRate,
        height / compressionRate,
        resources,
        resourceId
    )
    val destinationImage =
        Bitmap.createBitmap(sourceImage.width, sourceImage.height, Bitmap.Config.ARGB_8888)
    destinationImage.eraseColor(color)

    val bitmap = Bitmap.createBitmap(
        sourceImage.width,
        sourceImage.height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)

    val paint = Paint()
    canvas.drawBitmap(destinationImage, 0f, 0f, paint)

    val mode: PorterDuff.Mode = PorterDuff.Mode.DST_IN
    paint.xfermode = PorterDuffXfermode(mode)

    canvas.drawBitmap(sourceImage, 0f, 0f, paint)

    bitmap
}
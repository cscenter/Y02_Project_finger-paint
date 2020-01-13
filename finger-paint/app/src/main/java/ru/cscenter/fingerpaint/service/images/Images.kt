package ru.cscenter.fingerpaint.service.images

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import kotlin.math.min

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

fun getBitmapFromResource(
    width: Int,
    height: Int,
    resources: Resources,
    resourceId: Int
): Bitmap {
    val drawable: Drawable = ResourcesCompat.getDrawable(resources, resourceId, null)!!
    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    val side = min(width, height)
    val startX = (width - side) / 2
    val startY = (height - side) / 2
    drawable.setBounds(startX, startY, startX + side, startY + side)
    drawable.draw(canvas)
    return bitmap
}
package ru.cscenter.fingerpaint.ui.games.images

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat

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
    val canvas = Canvas()
    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)
    return bitmap
}
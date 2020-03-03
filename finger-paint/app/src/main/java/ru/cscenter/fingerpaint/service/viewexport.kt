package ru.cscenter.fingerpaint.service

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.view.View


fun exportViewToFile(activity: Activity, view: View, title: String) {
    val bitmap = createBitmap(view)!!
    @Suppress("DEPRECATION")
    val path = MediaStore.Images.Media.insertImage(activity.contentResolver, bitmap, title, null)
    val uri = Uri.parse(path)
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        type = "image/png"
    }
    activity.startActivity(intent)
}

private fun createBitmap(view: View): Bitmap? {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    view.draw(canvas)
    return bitmap
}

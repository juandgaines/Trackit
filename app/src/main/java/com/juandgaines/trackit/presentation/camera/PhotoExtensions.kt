package com.juandgaines.trackit.presentation.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(
    quality: Int = 90,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(format, quality, stream)
    return stream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}
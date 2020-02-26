package com.googy.files

import android.graphics.Bitmap
import androidx.core.graphics.scale
import java.io.File
import kotlin.math.roundToInt

fun File.writeScaledBitmap(bitmap: Bitmap, width: Int) {
    outputStream().use { out ->
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val height = (width / aspectRatio).roundToInt()
        bitmap.scale(width, height, false).compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
    }
}

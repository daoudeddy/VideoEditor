package com.googy.trim.view

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.googy.trim.R
import java.util.*


class Thumb private constructor() {

    var index: Int = 0
        private set
    var value: Float = 0.toFloat()
    var pos: Float = 0.toFloat()
    var bitmap: Bitmap? = null
        private set(bitmap) {
            field = bitmap
            widthBitmap = bitmap?.width ?: 0
            heightBitmap = bitmap?.height ?: 0
        }
    var widthBitmap: Int = 0
        private set
    private var heightBitmap: Int = 0

    var lastTouchX: Float = 0.toFloat()

    init {
        value = 0f
        pos = 0f
    }

    companion object {
        const val LEFT = 0
        const val RIGHT = 1

        fun initThumbs(resources: Resources): List<Thumb> {
            val thumbs = Vector<Thumb>()
            for (i in 0..1) {
                val th = Thumb()
                th.index = i
                val resImageLeft = R.drawable.ic_video_thumb_handle
                val bitmap = BitmapFactory.decodeResource(resources, resImageLeft)
                val width = bitmap.width
                val height = bitmap.height
                val newWidth = dpToPx(resources, 10)
                val newHeight = dpToPx(resources, 42)
                val scaleWidth = newWidth * 1.0f / width
                val scaleHeight = newHeight * 1.0f / height
                val matrix = Matrix()
                matrix.postScale(scaleWidth, scaleHeight)
                th.bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
                thumbs.add(th)
            }
            return thumbs
        }

        fun getWidthBitmap(thumbs: List<Thumb>): Int = thumbs[0].widthBitmap

        fun getHeightBitmap(thumbs: List<Thumb>): Int = thumbs[0].heightBitmap

        private fun dpToPx(resources: Resources, dp: Int): Int {
            return (dp * resources.displayMetrics.density + 0.5f).toInt()
        }
    }
}

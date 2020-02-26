package com.googy.videoeditor.ui.crop.cropview.util

import android.graphics.Rect

object AspectRatioUtil {

    fun calculateAspectRatio(left: Float, top: Float, right: Float, bottom: Float): Float {
        val width = right - left
        val height = bottom - top
        return width / height
    }

    fun calculateAspectRatio(rect: Rect): Float {
        return rect.width().toFloat() / rect.height().toFloat()
    }

    fun calculateLeft(top: Float, right: Float, bottom: Float, targetAspectRatio: Float): Float {
        val height = bottom - top
        return right - targetAspectRatio * height
    }

    fun calculateTop(left: Float, right: Float, bottom: Float, targetAspectRatio: Float): Float {
        val width = right - left
        return bottom - width / targetAspectRatio
    }

    fun calculateRight(left: Float, top: Float, bottom: Float, targetAspectRatio: Float): Float {
        val height = bottom - top
        return targetAspectRatio * height + left
    }

    fun calculateBottom(left: Float, top: Float, right: Float, targetAspectRatio: Float): Float {
        val width = right - left
        return width / targetAspectRatio + top
    }

    fun calculateWidth(top: Float, bottom: Float, targetAspectRatio: Float): Float {
        val height = bottom - top
        return targetAspectRatio * height
    }

    fun calculateHeight(left: Float, right: Float, targetAspectRatio: Float): Float {
        val width = right - left
        return width / targetAspectRatio
    }
}

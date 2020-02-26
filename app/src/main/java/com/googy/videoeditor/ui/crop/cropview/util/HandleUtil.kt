package com.googy.videoeditor.ui.crop.cropview.util

import android.content.Context
import android.util.Pair
import android.util.TypedValue

import com.googy.videoeditor.ui.crop.cropview.window.CropView
import com.googy.videoeditor.ui.crop.cropview.window.handle.Handle
import kotlin.math.abs

object HandleUtil {

    fun getTargetRadius(context: Context): Float {
        return TypedValue.applyDimension(1, 24.0f, context.resources.displayMetrics)
    }

    fun getPressedHandle(x: Float, y: Float, left: Float, top: Float, right: Float, bottom: Float, targetRadius: Float): Handle? {
        var pressedHandle: Handle? = null
        if (isInCornerTargetZone(x, y, left, top, targetRadius)) {
            pressedHandle = Handle.TOP_LEFT
        } else if (isInCornerTargetZone(x, y, right, top, targetRadius)) {
            pressedHandle = Handle.TOP_RIGHT
        } else if (isInCornerTargetZone(x, y, left, bottom, targetRadius)) {
            pressedHandle = Handle.BOTTOM_LEFT
        } else if (isInCornerTargetZone(x, y, right, bottom, targetRadius)) {
            pressedHandle = Handle.BOTTOM_RIGHT
        } else if (isInCenterTargetZone(x, y, left, top, right, bottom) && focusCenter()) {
            pressedHandle = Handle.CENTER
        } else if (isInHorizontalTargetZone(x, y, left, right, top, targetRadius)) {
            pressedHandle = Handle.TOP
        } else if (isInHorizontalTargetZone(x, y, left, right, bottom, targetRadius)) {
            pressedHandle = Handle.BOTTOM
        } else if (isInVerticalTargetZone(x, y, left, top, bottom, targetRadius)) {
            pressedHandle = Handle.LEFT
        } else if (isInVerticalTargetZone(x, y, right, top, bottom, targetRadius)) {
            pressedHandle = Handle.RIGHT
        } else if (isInCenterTargetZone(x, y, left, top, right, bottom) && !focusCenter()) {
            pressedHandle = Handle.CENTER
        }

        return pressedHandle
    }

    fun getOffset(handle: Handle?, x: Float, y: Float, left: Float, top: Float, right: Float, bottom: Float): Pair<Float, Float>? {
        if (handle == null) {
            return null
        } else {
            var touchOffsetX = 0.0f
            var touchOffsetY = 0.0f

            when (handle) {
                Handle.TOP_LEFT -> {
                    touchOffsetX = left - x
                    touchOffsetY = top - y
                }
                Handle.TOP_RIGHT -> {
                    touchOffsetX = right - x
                    touchOffsetY = top - y
                }
                Handle.BOTTOM_LEFT -> {
                    touchOffsetX = left - x
                    touchOffsetY = bottom - y
                }
                Handle.BOTTOM_RIGHT -> {
                    touchOffsetX = right - x
                    touchOffsetY = bottom - y
                }
                Handle.LEFT -> {
                    touchOffsetX = left - x
                    touchOffsetY = 0.0f
                }
                Handle.TOP -> {
                    touchOffsetX = 0.0f
                    touchOffsetY = top - y
                }
                Handle.RIGHT -> {
                    touchOffsetX = right - x
                    touchOffsetY = 0.0f
                }
                Handle.BOTTOM -> {
                    touchOffsetX = 0.0f
                    touchOffsetY = bottom - y
                }
                Handle.CENTER -> {
                    val centerX = (right + left) / 2.0f
                    val centerY = (top + bottom) / 2.0f
                    touchOffsetX = centerX - x
                    touchOffsetY = centerY - y
                }
            }

            return Pair(touchOffsetX, touchOffsetY)
        }
    }

    private fun isInCornerTargetZone(x: Float, y: Float, handleX: Float, handleY: Float, targetRadius: Float): Boolean {
        return abs(x - handleX) <= targetRadius && abs(y - handleY) <= targetRadius
    }

    private fun isInHorizontalTargetZone(x: Float, y: Float, handleXStart: Float, handleXEnd: Float, handleY: Float, targetRadius: Float): Boolean {
        return x > handleXStart && x < handleXEnd && abs(y - handleY) <= targetRadius
    }

    private fun isInVerticalTargetZone(x: Float, y: Float, handleX: Float, handleYStart: Float, handleYEnd: Float, targetRadius: Float): Boolean {
        return abs(x - handleX) <= targetRadius && y > handleYStart && y < handleYEnd
    }

    private fun isInCenterTargetZone(x: Float, y: Float, left: Float, top: Float, right: Float, bottom: Float): Boolean {
        return x > left && x < right && y > top && y < bottom
    }

    private fun focusCenter(): Boolean {
        return !CropView.showGuidelines()
    }
}

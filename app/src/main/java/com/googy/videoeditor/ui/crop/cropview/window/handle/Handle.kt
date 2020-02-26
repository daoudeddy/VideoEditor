package com.googy.videoeditor.ui.crop.cropview.window.handle

import android.graphics.Rect

import com.googy.videoeditor.ui.crop.cropview.window.edge.Edge

enum class Handle private constructor(private val mHelper: HandleHelper) {
    TOP_LEFT(CornerHandleHelper(Edge.TOP, Edge.LEFT)),
    TOP_RIGHT(CornerHandleHelper(Edge.TOP, Edge.RIGHT)),
    BOTTOM_LEFT(CornerHandleHelper(Edge.BOTTOM, Edge.LEFT)),
    BOTTOM_RIGHT(CornerHandleHelper(Edge.BOTTOM, Edge.RIGHT)),
    LEFT(VerticalHandleHelper(Edge.LEFT)),
    TOP(HorizontalHandleHelper(Edge.TOP)),
    RIGHT(VerticalHandleHelper(Edge.RIGHT)),
    BOTTOM(HorizontalHandleHelper(Edge.BOTTOM)),
    CENTER(CenterHandleHelper());

    fun updateCropWindow(x: Float, y: Float, imageRect: Rect, snapRadius: Float) {
        this.mHelper.updateCropWindow(x, y, imageRect, snapRadius)
    }

    fun updateCropWindow(x: Float, y: Float, targetAspectRatio: Float, imageRect: Rect, snapRadius: Float) {
        this.mHelper.updateCropWindow(x, y, targetAspectRatio, imageRect, snapRadius)
    }
}

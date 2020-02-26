package com.googy.videoeditor.ui.crop.cropview.window.handle

import android.graphics.Rect

import com.googy.videoeditor.ui.crop.cropview.window.edge.Edge

internal class CenterHandleHelper : HandleHelper(null, null) {

    override fun updateCropWindow(x: Float, y: Float, imageRect: Rect, snapRadius: Float) {
        val left = Edge.LEFT.coordinate
        val top = Edge.TOP.coordinate
        val right = Edge.RIGHT.coordinate
        val bottom = Edge.BOTTOM.coordinate
        val currentCenterX = (left + right) / 2.0f
        val currentCenterY = (top + bottom) / 2.0f
        val offsetX = x - currentCenterX
        val offsetY = y - currentCenterY
        Edge.LEFT.offset(offsetX)
        Edge.TOP.offset(offsetY)
        Edge.RIGHT.offset(offsetX)
        Edge.BOTTOM.offset(offsetY)

        var offset: Float
        if (Edge.LEFT.isOutsideMargin(imageRect, snapRadius)) {
            offset = Edge.LEFT.snapToRect(imageRect)
            Edge.RIGHT.offset(offset)
        } else if (Edge.RIGHT.isOutsideMargin(imageRect, snapRadius)) {
            offset = Edge.RIGHT.snapToRect(imageRect)
            Edge.LEFT.offset(offset)
        }

        if (Edge.TOP.isOutsideMargin(imageRect, snapRadius)) {
            offset = Edge.TOP.snapToRect(imageRect)
            Edge.BOTTOM.offset(offset)
        } else if (Edge.BOTTOM.isOutsideMargin(imageRect, snapRadius)) {
            offset = Edge.BOTTOM.snapToRect(imageRect)
            Edge.TOP.offset(offset)
        }
    }

    override fun updateCropWindow(x: Float, y: Float, targetAspectRatio: Float, imageRect: Rect, snapRadius: Float) {
        this.updateCropWindow(x, y, imageRect, snapRadius)
    }
}


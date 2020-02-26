package com.googy.videoeditor.ui.crop.cropview.window.handle

import android.graphics.Rect
import com.googy.videoeditor.ui.crop.cropview.window.edge.Edge

internal class CornerHandleHelper(horizontalEdge: Edge, verticalEdge: Edge) : HandleHelper(horizontalEdge, verticalEdge) {

    override fun updateCropWindow(x: Float, y: Float, targetAspectRatio: Float, imageRect: Rect, snapRadius: Float) {
        val (primaryEdge, secondaryEdge) = this.getActiveEdges(x, y, targetAspectRatio)
        primaryEdge?.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio)
        secondaryEdge?.adjustCoordinate(targetAspectRatio)
        if (secondaryEdge?.isOutsideMargin(imageRect, snapRadius) == true) {
            secondaryEdge.snapToRect(imageRect)
            primaryEdge?.adjustCoordinate(targetAspectRatio)
        }
    }
}
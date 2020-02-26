package com.googy.videoeditor.ui.crop.cropview.window.handle

import android.graphics.Rect

import com.googy.videoeditor.ui.crop.cropview.util.AspectRatioUtil
import com.googy.videoeditor.ui.crop.cropview.window.edge.Edge
import com.googy.videoeditor.ui.crop.cropview.window.edge.EdgePair

internal abstract class HandleHelper(private val mHorizontalEdge: Edge?, private val mVerticalEdge: Edge?) {
    private val activeEdges: EdgePair = EdgePair(mHorizontalEdge, mVerticalEdge)

    open fun updateCropWindow(x: Float, y: Float, imageRect: Rect, snapRadius: Float) {
        val activeEdges = activeEdges
        val primaryEdge = activeEdges.primary
        val secondaryEdge = activeEdges.secondary
        primaryEdge?.adjustCoordinate(x, y, imageRect, snapRadius, 1.0f)
        secondaryEdge?.adjustCoordinate(x, y, imageRect, snapRadius, 1.0f)
    }

    internal abstract fun updateCropWindow(x: Float, y: Float, targetAspectRatio: Float, imageRect: Rect, snapRadius: Float)

    fun getActiveEdges(x: Float, y: Float, targetAspectRatio: Float): EdgePair {
        val potentialAspectRatio = getAspectRatio(x, y)
        if (potentialAspectRatio > targetAspectRatio) {
            activeEdges.primary = mVerticalEdge
            activeEdges.secondary = mHorizontalEdge
        } else {
            activeEdges.primary = mHorizontalEdge
            activeEdges.secondary = mVerticalEdge
        }
        return activeEdges
    }

    private fun getAspectRatio(x: Float, y: Float): Float {
        val left = if (mVerticalEdge === Edge.LEFT) x else Edge.LEFT.coordinate
        val top = if (mHorizontalEdge === Edge.TOP) y else Edge.TOP.coordinate
        val right = if (mVerticalEdge === Edge.RIGHT) x else Edge.RIGHT.coordinate
        val bottom = if (mHorizontalEdge === Edge.BOTTOM) y else Edge.BOTTOM.coordinate
        return AspectRatioUtil.calculateAspectRatio(left, top, right, bottom)
    }

    companion object {
        private val UNFIXED_ASPECT_RATIO_CONSTANT = 1.0f
    }
}


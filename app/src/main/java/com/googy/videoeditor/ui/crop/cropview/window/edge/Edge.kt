package com.googy.videoeditor.ui.crop.cropview.window.edge

import android.graphics.Rect
import android.view.View

import com.googy.videoeditor.ui.crop.cropview.util.AspectRatioUtil
import kotlin.math.max
import kotlin.math.min

enum class Edge() {
    LEFT,
    TOP,
    RIGHT,
    BOTTOM;

    var coordinate: Float = 0.toFloat()

    fun offset(distance: Float) {
        this.coordinate += distance
    }

    fun adjustCoordinate(x: Float, y: Float, imageRect: Rect, imageSnapRadius: Float, aspectRatio: Float) {
        when (this) {
            LEFT -> this.coordinate = adjustLeft(x, imageRect, imageSnapRadius, aspectRatio)
            TOP -> this.coordinate = adjustTop(y, imageRect, imageSnapRadius, aspectRatio)
            RIGHT -> this.coordinate = adjustRight(x, imageRect, imageSnapRadius, aspectRatio)
            BOTTOM -> this.coordinate = adjustBottom(y, imageRect, imageSnapRadius, aspectRatio)
        }

    }

    fun adjustCoordinate(aspectRatio: Float) {
        val left = LEFT.coordinate
        val top = TOP.coordinate
        val right = RIGHT.coordinate
        val bottom = BOTTOM.coordinate
        when (this) {
            LEFT -> this.coordinate = AspectRatioUtil.calculateLeft(top, right, bottom, aspectRatio)
            TOP -> this.coordinate = AspectRatioUtil.calculateTop(left, right, bottom, aspectRatio)
            RIGHT -> this.coordinate = AspectRatioUtil.calculateRight(left, top, bottom, aspectRatio)
            BOTTOM -> this.coordinate = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio)
        }

    }

    fun isNewRectangleOutOfBounds(edge: Edge, imageRect: Rect, aspectRatio: Float): Boolean {
        val offset = edge.snapOffset(imageRect)
        val right: Float
        val left: Float
        val top: Float
        val bottom: Float
        when (this) {
            LEFT -> {
                if (edge == TOP) {
                    right = imageRect.top.toFloat()
                    left = BOTTOM.coordinate - offset
                    top = RIGHT.coordinate
                    bottom = AspectRatioUtil.calculateLeft(right, top, left, aspectRatio)
                    return this.isOutOfBounds(right, bottom, left, top, imageRect)
                }

                if (edge == BOTTOM) {
                    right = imageRect.bottom.toFloat()
                    left = TOP.coordinate - offset
                    top = RIGHT.coordinate
                    bottom = AspectRatioUtil.calculateLeft(left, top, right, aspectRatio)
                    return this.isOutOfBounds(left, bottom, right, top, imageRect)
                }
            }
            TOP -> {
                if (edge == LEFT) {
                    right = imageRect.left.toFloat()
                    left = RIGHT.coordinate - offset
                    top = BOTTOM.coordinate
                    bottom = AspectRatioUtil.calculateTop(right, left, top, aspectRatio)
                    return this.isOutOfBounds(bottom, right, top, left, imageRect)
                }

                if (edge == RIGHT) {
                    right = imageRect.right.toFloat()
                    left = LEFT.coordinate - offset
                    top = BOTTOM.coordinate
                    bottom = AspectRatioUtil.calculateTop(left, right, top, aspectRatio)
                    return this.isOutOfBounds(bottom, left, top, right, imageRect)
                }
            }
            RIGHT -> {
                if (edge == TOP) {
                    right = imageRect.top.toFloat()
                    left = BOTTOM.coordinate - offset
                    top = LEFT.coordinate
                    bottom = AspectRatioUtil.calculateRight(top, right, left, aspectRatio)
                    return this.isOutOfBounds(right, top, left, bottom, imageRect)
                }

                if (edge == BOTTOM) {
                    right = imageRect.bottom.toFloat()
                    left = TOP.coordinate - offset
                    top = LEFT.coordinate
                    bottom = AspectRatioUtil.calculateRight(top, left, right, aspectRatio)
                    return this.isOutOfBounds(left, top, right, bottom, imageRect)
                }
            }
            BOTTOM -> {
                if (edge == LEFT) {
                    right = imageRect.left.toFloat()
                    left = RIGHT.coordinate - offset
                    top = TOP.coordinate
                    bottom = AspectRatioUtil.calculateBottom(right, top, left, aspectRatio)
                    return this.isOutOfBounds(top, right, bottom, left, imageRect)
                }

                if (edge == RIGHT) {
                    right = imageRect.right.toFloat()
                    left = LEFT.coordinate - offset
                    top = TOP.coordinate
                    bottom = AspectRatioUtil.calculateBottom(left, top, right, aspectRatio)
                    return this.isOutOfBounds(top, left, bottom, right, imageRect)
                }
            }
        }

        return true
    }

    private fun isOutOfBounds(top: Float, left: Float, bottom: Float, right: Float, imageRect: Rect): Boolean {
        return top < imageRect.top.toFloat() || left < imageRect.left.toFloat() || bottom > imageRect.bottom.toFloat() || right > imageRect.right.toFloat()
    }

    fun snapToRect(imageRect: Rect): Float {
        val oldCoordinate = this.coordinate
        when (this) {
            LEFT -> this.coordinate = imageRect.left.toFloat()
            TOP -> this.coordinate = imageRect.top.toFloat()
            RIGHT -> this.coordinate = imageRect.right.toFloat()
            BOTTOM -> this.coordinate = imageRect.bottom.toFloat()
        }

        return coordinate - oldCoordinate
    }

    fun snapOffset(imageRect: Rect): Float {
        val oldCoordinate = this.coordinate
        val newCoordinate: Float = when (this) {
            LEFT -> imageRect.left.toFloat()
            TOP -> imageRect.top.toFloat()
            RIGHT -> imageRect.right.toFloat()
            BOTTOM -> imageRect.bottom.toFloat()
        }
        return newCoordinate - oldCoordinate
    }

    fun snapToView(view: View) {
        when (this) {
            LEFT -> this.coordinate = 0.0f
            TOP -> this.coordinate = 0.0f
            RIGHT -> this.coordinate = view.width.toFloat()
            BOTTOM -> this.coordinate = view.height.toFloat()
        }

    }

    fun isOutsideMargin(rect: Rect, margin: Float): Boolean {
        return when (this) {
            LEFT -> this.coordinate - rect.left.toFloat() < margin
            TOP -> this.coordinate - rect.top.toFloat() < margin
            RIGHT -> rect.right.toFloat() - this.coordinate < margin
            BOTTOM -> rect.bottom.toFloat() - this.coordinate < margin
        }
    }

    fun isOutsideFrame(rect: Rect): Boolean {
        val margin = 0.0
        return when (this) {
            LEFT -> (this.coordinate - rect.left.toFloat()).toDouble() < margin
            TOP -> (this.coordinate - rect.top.toFloat()).toDouble() < margin
            RIGHT -> (rect.right.toFloat() - this.coordinate).toDouble() < margin
            BOTTOM -> (rect.bottom.toFloat() - this.coordinate).toDouble() < margin
        }
    }

    companion object {

        val MIN_CROP_LENGTH_PX = 40

        val width: Float
            get() = RIGHT.coordinate - LEFT.coordinate

        val height: Float
            get() = BOTTOM.coordinate - TOP.coordinate

        private fun adjustLeft(x: Float, imageRect: Rect, imageSnapRadius: Float, aspectRatio: Float): Float {
            val resultX: Float
            if (x - imageRect.left.toFloat() < imageSnapRadius) {
                resultX = imageRect.left.toFloat()
            } else {
                var resultXHoriz = java.lang.Float.POSITIVE_INFINITY
                var resultXVert = java.lang.Float.POSITIVE_INFINITY
                if (x >= RIGHT.coordinate - 40.0f) {
                    resultXHoriz = RIGHT.coordinate - 40.0f
                }

                if ((RIGHT.coordinate - x) / aspectRatio <= 40.0f) {
                    resultXVert = RIGHT.coordinate - 40.0f * aspectRatio
                }

                resultX = min(x, min(resultXHoriz, resultXVert))
            }

            return resultX
        }

        private fun adjustRight(x: Float, imageRect: Rect, imageSnapRadius: Float, aspectRatio: Float): Float {
            val resultX: Float
            if (imageRect.right.toFloat() - x < imageSnapRadius) {
                resultX = imageRect.right.toFloat()
            } else {
                var resultXHoriz = java.lang.Float.NEGATIVE_INFINITY
                var resultXVert = java.lang.Float.NEGATIVE_INFINITY
                if (x <= LEFT.coordinate + 40.0f) {
                    resultXHoriz = LEFT.coordinate + 40.0f
                }

                if ((x - LEFT.coordinate) / aspectRatio <= 40.0f) {
                    resultXVert = LEFT.coordinate + 40.0f * aspectRatio
                }

                resultX = max(x, max(resultXHoriz, resultXVert))
            }

            return resultX
        }

        private fun adjustTop(y: Float, imageRect: Rect, imageSnapRadius: Float, aspectRatio: Float): Float {
            val resultY: Float
            if (y - imageRect.top.toFloat() < imageSnapRadius) {
                resultY = imageRect.top.toFloat()
            } else {
                var resultYVert = java.lang.Float.POSITIVE_INFINITY
                var resultYHoriz = java.lang.Float.POSITIVE_INFINITY
                if (y >= BOTTOM.coordinate - 40.0f) {
                    resultYHoriz = BOTTOM.coordinate - 40.0f
                }

                if ((BOTTOM.coordinate - y) * aspectRatio <= 40.0f) {
                    resultYVert = BOTTOM.coordinate - 40.0f / aspectRatio
                }

                resultY = min(y, min(resultYHoriz, resultYVert))
            }

            return resultY
        }

        private fun adjustBottom(y: Float, imageRect: Rect, imageSnapRadius: Float, aspectRatio: Float): Float {
            val resultY: Float
            if (imageRect.bottom.toFloat() - y < imageSnapRadius) {
                resultY = imageRect.bottom.toFloat()
            } else {
                var resultYVert = java.lang.Float.NEGATIVE_INFINITY
                var resultYHoriz = java.lang.Float.NEGATIVE_INFINITY
                if (y <= TOP.coordinate + 40.0f) {
                    resultYVert = TOP.coordinate + 40.0f
                }

                if ((y - TOP.coordinate) * aspectRatio <= 40.0f) {
                    resultYHoriz = TOP.coordinate + 40.0f / aspectRatio
                }

                resultY = max(y, max(resultYHoriz, resultYVert))
            }

            return resultY
        }
    }
}


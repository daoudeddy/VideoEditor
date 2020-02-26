package com.googy.videoeditor.ui.crop.cropview.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue

object PaintUtil {
    private val DEFAULT_CORNER_COLOR = -1
    private val SEMI_TRANSPARENT = "#AAFFFFFF"
    private val DEFAULT_BACKGROUND_COLOR_ID = "#B0000000"
    private val DEFAULT_LINE_THICKNESS_DP = 3.0f
    private val DEFAULT_CORNER_THICKNESS_DP = 5.0f
    private val DEFAULT_GUIDELINE_THICKNESS_PX = 1.0f

    const val cornerThickness: Float = 5.0f
    const val lineThickness: Float = 3.0f

    fun newBorderPaint(context: Context): Paint {
        val lineThicknessPx = TypedValue.applyDimension(1, 3.0f, context.resources.displayMetrics)
        val borderPaint = Paint()
        borderPaint.color = Color.parseColor("#AAFFFFFF")
        borderPaint.strokeWidth = lineThicknessPx
        borderPaint.style = Paint.Style.STROKE
        return borderPaint
    }

    fun newGuidelinePaint(): Paint {
        val paint = Paint()
        paint.color = Color.parseColor("#AAFFFFFF")
        paint.strokeWidth = 1.0f
        return paint
    }

    fun newBackgroundPaint(): Paint {
        val paint = Paint()
        paint.color = Color.parseColor("#B0000000")
        return paint
    }

    fun newCornerPaint(context: Context): Paint {
        val lineThicknessPx = TypedValue.applyDimension(1, 5.0f, context.resources.displayMetrics)
        val cornerPaint = Paint()
        cornerPaint.color = -1
        cornerPaint.strokeWidth = lineThicknessPx
        cornerPaint.style = Paint.Style.STROKE
        return cornerPaint
    }
}

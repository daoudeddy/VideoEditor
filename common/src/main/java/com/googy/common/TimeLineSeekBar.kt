package com.googy.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

class TimeLineSeekBar constructor(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {
    companion object {
        private const val FORMAT_TIMELINE = "mm:ss"
    }

    private val paint = Paint(ANTI_ALIAS_FLAG)
    private val textPaint = Paint(ANTI_ALIAS_FLAG)
    private val cyCircle =
        resources.getDimension(R.dimen.playing_video_seekbar_time_line_height) - resources.getDimension(
            R.dimen.playing_video_frames_video_height
        ) - resources.getDimension(R.dimen.playing_video_margin_circle_with_timeline)
    private val radiusCircle = resources.getDimension(R.dimen.playing_video_radius_circle)
    private val cyText =
        cyCircle - radiusCircle - resources.getDimension(R.dimen.playing_video_text_seekbar_margin)
    private val timeFormat = SimpleDateFormat(FORMAT_TIMELINE, Locale.ENGLISH)

    private var durationVideo = 0f
    private var isCenter = false
    private var cxCurrentProgress = 0f
    private var currentDuration = 0f
    private var progressListener: ((Float) -> Unit)? = null

    init {
        paint.color = Color.WHITE
        paint.strokeWidth = resources.getDimension(R.dimen.playing_video_line_width)
        textPaint.color = Color.WHITE
        textPaint.textSize = resources.getDimension(R.dimen.playing_video_size_text_paint)
    }

    override fun onDraw(canvas: Canvas?) {
        if (isCenter) {
            cxCurrentProgress = width / 2f
        }
        canvas?.drawText(
            timeFormat.format(currentDuration),
            cxCurrentProgress - textPaint.textSize * 1.35f,
            cyText,
            textPaint
        )
        canvas?.drawCircle(cxCurrentProgress, cyCircle, radiusCircle, paint)
        canvas?.drawLine(cxCurrentProgress, cyCircle, cxCurrentProgress, height.toFloat(), paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isCenter) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    cxCurrentProgress = event.x
                    currentDuration = cxCurrentProgress / width * durationVideo
                    progressListener?.invoke(currentDuration)
                    invalidate()
                }
            }
        }

        return true
    }

    fun setOnProgressListener(listener: (Float) -> Unit) {
        progressListener = listener
    }

    fun setViewToCenter() {
        isCenter = true
        invalidate()
    }

    fun updateTimePosition(duration: Float) {
        currentDuration = duration
        invalidate()
    }

}
package com.googy.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import androidx.core.view.GestureDetectorCompat
import kotlinx.coroutines.Job

class CustomHorizontalScrollView constructor(
    context: Context,
    attrs: AttributeSet
) : HorizontalScrollView(context, attrs), GestureDetector.OnGestureListener, OnFlingListener {

    companion object {
        private const val TIME_INTERVAL = 20L
        private const val TIME_INTERVAL_CHECKER = 40L
    }

    private val detector: GestureDetectorCompat = GestureDetectorCompat(context, this)

    var isSeeking = false
    var isPaused = false
    private var job: Job? = null
    private var videoController: VideoController? = null
    private var durationVideo = 0f
    private var widthThumbnails = 0
    private var previousPosition = 0
    private var flingChecker: Runnable? = null
    private var timeController: Runnable? = null

    init {
        flingChecker = Runnable {
            val currentPosition = scrollX
            if (previousPosition - currentPosition == 0) {
                onFlingStopped()
                removeCallbacks(flingChecker)
            } else {
                previousPosition = scrollX
                postDelayed(flingChecker, TIME_INTERVAL_CHECKER)
            }
        }

        timeController = Runnable {
            val currentPosition = videoController?.getCurrentPositionVideo()
            currentPosition?.let {
                val scrollX = currentPosition / durationVideo * widthThumbnails
                scrollTo(scrollX.toInt(), 0)
                videoController?.updateTimeLineSeekBar(currentPosition.toFloat())
                postDelayed(timeController, TIME_INTERVAL)
            }
        }
    }

    fun setVideoController(videoController: VideoController?) {
        this.videoController = videoController
    }

    fun setPropertyVideo(durationVideo: Float, widthThumbnails: Int) {
        this.durationVideo = durationVideo
        this.widthThumbnails = widthThumbnails
    }

    fun subscribeTimeController() {
        postDelayed(timeController, TIME_INTERVAL)
    }

    fun unsubscribeTimeController() {
        removeCallbacks(timeController)
    }

    fun isEnded(): Boolean {
        return scrollX == widthThumbnails
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job?.cancel()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                videoController?.keepFrameVideo()
                unsubscribeTimeController()
            }
            MotionEvent.ACTION_UP -> {
                isSeeking = false
                if (!isPaused) {
                    videoController?.unKeepFrameVideo()
                    subscribeTimeController()
                }
            }
        }

        return if (detector.onTouchEvent(ev)) {
            true
        } else {
            super.onTouchEvent(ev)
        }
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        isSeeking = true
        onFlingStarted()
        removeCallbacks(flingChecker)
        post(flingChecker)

        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        val scrollX = scrollX.toFloat()
        val currentPositionVideo = scrollX / widthThumbnails * durationVideo
        videoController?.seekVideoTo(currentPositionVideo)
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        isSeeking = true
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        if (isSeeking) {
            val scrollX = scrollX.toFloat()
            val currentDurationVideo = scrollX / widthThumbnails * durationVideo
            videoController?.seekVideoTo(currentDurationVideo)
        }
    }

    override fun onFlingStarted() {

    }

    override fun onFlingStopped() {
        if (isSeeking && !isPaused) {
            videoController?.unKeepFrameVideo()
            subscribeTimeController()
            isSeeking = false
        }
        if (isEnded()) {
            videoController?.onPauseVideo()
            unsubscribeTimeController()
        }
    }

    interface VideoController {
        fun onPauseVideo()

        fun getCurrentPositionVideo(): Int

        fun videoIsPlaying(): Boolean

        fun seekVideoTo(currentPosition: Float)

        fun updateTimeLineSeekBar(currentPosition: Float)

        fun keepFrameVideo()

        fun unKeepFrameVideo()
    }
}

interface OnFlingListener {
    fun onFlingStarted()

    fun onFlingStopped()
}
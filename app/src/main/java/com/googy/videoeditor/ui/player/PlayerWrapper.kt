package com.googy.videoeditor.ui.player

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.googy.common.CustomHorizontalScrollView
import com.googy.common.extension.hide
import com.googy.common.extension.show
import com.googy.crop.utils.CroppedRect
import com.googy.filters.GLRenderSurfaceView
import com.googy.filters.gpuimage.glfilters.GPUImageFilter
import com.googy.trim.view.RangeSeekBarView
import com.googy.videoeditor.R
import com.googy.videoeditor.ui.base.TimeLineView
import kotlinx.android.synthetic.main.player_wrapper.view.*
import java.util.*


class PlayerWrapper : ConstraintLayout, MediaPlayer.OnPreparedListener,
    TimeLineView.OnSeekBarChangeListener, GLRenderSurfaceView.PlayerProgressListener,
    RangeSeekBarView.OnRangeSeekBarChangeListener, View.OnTouchListener {

    private var mDuration = 0
    private var mTimeVideo = 0
    private var mStartPosition = 0
    private var mEndPosition = 0

    private var cropEnabled = false
    private var trimEnabled = false
    private var timelineEnabled = false
    private var gridEnabled = false
    private var gridsCount = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.player_wrapper, this, true)
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.PlayerWrapper, 0, 0)
            cropEnabled = ta.getBoolean(R.styleable.PlayerWrapper_cropEnabled, false)
            trimEnabled = ta.getBoolean(R.styleable.PlayerWrapper_trimEnabled, false)
            timelineEnabled = ta.getBoolean(R.styleable.PlayerWrapper_timeLineEnabled, false)
            gridEnabled = ta.getBoolean(R.styleable.PlayerWrapper_gridEnabled, false)
            gridsCount = ta.getInteger(R.styleable.PlayerWrapper_gridsCount, 8)
            ta.recycle()
        }
        playerView.setOnTouchListener(this)
    }

    fun init(videoPath: String) {
        playerView.init(videoPath, this)

        if (timelineEnabled) {
            timeLineView.initTimeLineView(videoPath, this)
            playerView.setPlayerProgressLisnener(this)
//            handlerTop.setOnSeekBarChangeListener(this)
//            playerController.show()
        } else {
//            playerController.hide()
        }

//        if (trimEnabled) {
//            timeLineBar.show()
//        } else {
//            timeLineBar.hide()
//        }

        if (gridEnabled) {
            gridView.show()
            gridView.setGridCount(gridsCount)
        } else {
            gridView.hide()
        }
    }

    private val videoController =
        object : CustomHorizontalScrollView.VideoController {

            override fun onPauseVideo() {
                playerView.pause()
            }

            override fun getCurrentPositionVideo(): Int {
                return playerView.currentPosition()
            }

            override fun videoIsPlaying(): Boolean {
                return playerView.isPlaying()
            }

            override fun seekVideoTo(currentPosition: Float) {
                playerView.seek(currentPosition.toInt())
            }

            override fun updateTimeLineSeekBar(currentPosition: Float) {
//                timeLineView.updateTimePosition(currentPosition)
            }

            override fun keepFrameVideo() {
                playerView.pause()
            }

            override fun unKeepFrameVideo() {
                playerView.resume()
            }

        }

    fun showGuideLines() = gridView.show()
    fun hideGuideLines() = gridView.hide()

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) playerView.resume()
        else playerView.pause()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        playerView.destroy()
//        timeLineView.removeController()
//        timeLineView.destroy()
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        playerView.onPrepared(mediaPlayer)
        playerRootView.layoutParams = layoutParams(mediaPlayer.videoWidth, mediaPlayer.videoHeight)
//        handlerTop.max = mediaPlayer.duration
//        timeLineView.startVideo()
        if (cropEnabled) {
            cropOverlayView.show()
        } else {
            cropOverlayView.hide()
        }

        initTrimmer(mediaPlayer)
    }

    private fun initTrimmer(mediaPlayer: MediaPlayer) {
        if (trimEnabled) {

            mDuration = mediaPlayer.duration
            mStartPosition = 0
            mEndPosition = mDuration
            mTimeVideo = mEndPosition

//            timeLineBar.setRange(mStartPosition, mEndPosition)
//            timeLineBar.selectedMinValue = 0
//            timeLineBar.selectedMaxValue = mEndPosition
//            timeLineBar.isNotifyWhileDragging = true
//            timeLineBar.setOnRangeSeekBarChangeListener(this)
        }
    }

    private fun layoutParams(w: Int, h: Int): LayoutParams {
        return if (h > w) {
            playerRootView.layoutParams.apply {
                width = w * playerContainer.measuredHeight / h
                height = playerContainer.measuredHeight
            } as LayoutParams
        } else
            playerRootView.layoutParams.apply {
                width = playerContainer.measuredWidth
                height = h * playerContainer.measuredWidth / w
            } as LayoutParams
    }

    override fun onProgressChanged(progress: Int) {
        if (!trimEnabled) playerView.seek(progress)
        else if (trimEnabled) setProgressBarPosition(progress)
    }

    private fun setProgressBarPosition(position: Int) {
        val newPosition = when {
            position < mStartPosition -> mStartPosition
            position > mEndPosition -> mEndPosition
            else -> position
        }
//        handlerTop.progress = newPosition
        playerView.seek(newPosition)
    }

    override fun onPlayerProgressChanged(time: Int) {
        timeLineView.scrollTo(time)
        when {
            time > mEndPosition && trimEnabled -> {
//                handlerTop.progress = mStartPosition
                playerView.seek(mStartPosition)

            }
            else -> {
//                handlerTop.progress = time
            }
        }
    }

    override fun onRangeSeekBarValuesChanged(
        bar: RangeSeekBarView?,
        minValue: Int,
        maxValue: Int,
        action: Int,
        isMin: Boolean,
        pressedThumb: RangeSeekBarView.Thumb
    ) {
        when (action) {
            MotionEvent.ACTION_UP -> {
                onStopSeekThumbs()
            }
            MotionEvent.ACTION_DOWN -> {
                onStartSeekThumbs()
            }
            MotionEvent.ACTION_MOVE -> {
                onSeekThumbs(minValue, maxValue, pressedThumb)
            }
        }
    }

    private fun onStopSeekThumbs() {
        playerView.resume()
//        handlerTop.visibility = View.VISIBLE
    }

    private fun onStartSeekThumbs() {
        playerView.pause()
//        handlerTop.visibility = View.GONE
    }

    private fun onSeekThumbs(
        minValue: Int,
        maxValue: Int,
        pressedThumb: RangeSeekBarView.Thumb
    ) {
        mStartPosition = minValue
        mEndPosition = maxValue

        if (pressedThumb == RangeSeekBarView.Thumb.MAX) {
            playerView.seek(mEndPosition)
        } else {
            playerView.seek(mStartPosition)
        }

//        if (mStartPosition > handlerTop.progress) {
//            handlerTop.progress = mStartPosition
//        }
//        if (mEndPosition < handlerTop.progress) {
//            handlerTop.progress = mEndPosition
//        }

        setTimeFrames()
        mTimeVideo = mEndPosition - mStartPosition
    }

    private fun setTimeFrames() {
//        textTimeSelection.text = String.format(
//            "%s - %s",
//            stringForTime(mStartPosition),
//            stringForTime(mEndPosition)
//        )
    }

    override fun onStartTrackingTouch() {
        playerView.pause()
    }

    override fun onStopTrackingTouch() {
        playerView.resume()
    }

    private fun stringForTime(timeMs: Int): String {
        val totalSeconds = (timeMs / 1000)
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        val mFormatter = Formatter()
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                true
            }
            MotionEvent.ACTION_UP -> {
                if (playerView.isPlaying()) {
                    playerView.pause()
//                    timeLineView.pauseVideo()
                } else {
                    playerView.resume()
//                    timeLineView.startVideo()
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                true
            }
            else -> false
        }
    }

    fun previewFilter(filter: GPUImageFilter) {
        playerView.previewFilter(filter)
    }

    fun applyFilter(filter: GPUImageFilter) {
        playerView.applyFilter(filter)
    }

    fun applyFilters(filters: MutableList<GPUImageFilter>) {
        playerView.applyFilters(filters)
    }

    fun resetToAppliedFilters() {
        playerView.resetToAppliedFilters()
    }

    fun resetFilters() {
        playerView.resetFilters()
    }

    fun setSpeed(speed: Float) {
        playerView.setSpeed(speed)
    }

    fun setFixedAspectRatio(fixAspectRatio: Boolean) {
        cropOverlayView.setFixedAspectRatio(fixAspectRatio)
    }

    fun setAspectRatio(aspectRatioX: Int, aspectRatioY: Int) {
        cropOverlayView.setAspectRatioX(aspectRatioX)
        cropOverlayView.setAspectRatioY(aspectRatioY)
    }

    fun getCropRect(): CroppedRect {
        return cropOverlayView.getCropRect(playerView.videoWidth(), playerView.videoHeight())
    }

    fun onVideoSizeChanged(width: Float, height: Float) {
        playerView.setScaledSize(width, height)
        playerRootView.layoutParams =
            layoutParams(playerView.videoWidth(), playerView.videoHeight())
    }
}
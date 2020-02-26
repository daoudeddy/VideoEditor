package com.googy.trim

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import com.googy.common.utils.BackgroundExecutor
import com.googy.common.utils.UiThreadExecutor
import com.googy.trim.interfaces.OnProgressVideoListener
import com.googy.trim.interfaces.OnTrimVideoListener
import com.googy.trim.interfaces.OnVideoListener
import kotlinx.android.synthetic.main.view_time_line.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


class VideoTrimmer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var mSrc: Uri

    private var mMaxDuration: Int = -1
    private var mMinDuration: Int = -1
    private var mListener: OnProgressVideoListener? = null

    private var mOnTrimVideoListener: OnTrimVideoListener? = null
    private var mOnVideoListener: OnVideoListener? = null

    private var mDuration = 0
    private var mTimeVideo = 0
    private var mStartPosition = 0
    private var mEndPosition = 0

    private var mResetSeekBar = true
    private var firstTime = true
    private val playerTimer = PlayerTimer()

    init {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_time_line, this, true)
        setUpListeners()
    }

    private fun setUpListeners() {
        val gestureDetector = GestureDetector(context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    onClickVideoPlayPause()
                    return true
                }
            }
        )

        video_loader.setOnErrorListener { _, what, _ ->
            mOnTrimVideoListener?.onError("Something went wrong reason : $what")
            false
        }

        video_loader.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        handlerTop.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                onPlayerIndicatorSeekChanged(progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                onPlayerIndicatorSeekStart()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                onPlayerIndicatorSeekStop(seekBar)
            }
        })

        timeLineBar.setOnRangeSeekBarChangeListener { bar, minValue, maxValue, action, isMin, pressedThumb ->
            when (action) {
                MotionEvent.ACTION_UP -> {
                    onStopSeekThumbs()
                    handlerTop.visibility = View.VISIBLE
                }
                MotionEvent.ACTION_DOWN -> {
                    handlerTop.visibility = View.GONE
                }
                MotionEvent.ACTION_MOVE -> {
                    onSeekThumbs(minValue, maxValue)
                    playerTimer.pauseTimer()
                }
            }
        }

        video_loader.setOnPreparedListener { mp ->
            if (firstTime) {
                onVideoPrepared(mp)
                firstTime = false
            }
        }
        video_loader.setOnCompletionListener { onVideoCompleted() }
    }

    private fun onPlayerIndicatorSeekChanged(progress: Int, fromUser: Boolean) {
        if (fromUser) {
            if (progress < mStartPosition) {
                setProgressBarPosition(mStartPosition)
            } else if (progress > mEndPosition) {
                setProgressBarPosition(mEndPosition)
            }
        }
    }

    private fun onPlayerIndicatorSeekStart() {
        video_loader.pause()
        icon_video_play.visibility = View.VISIBLE
        playerTimer.pauseTimer()
        notifyProgressUpdate()
    }

    private fun onPlayerIndicatorSeekStop(seekBar: SeekBar) {
        video_loader.pause()
        icon_video_play.visibility = View.VISIBLE
        val duration = seekBar.progress
        video_loader.seekTo(duration)
        playerTimer.startTimer()
        notifyProgressUpdate()
    }

    private fun setProgressBarPosition(position: Int) {
        when {
            position < mStartPosition -> handlerTop.progress = mStartPosition
            position > mEndPosition -> handlerTop.progress = mEndPosition
            else -> handlerTop.progress = position
        }
    }

//    fun onSaveClicked() {
//        mOnTrimVideoListener?.onTrimStarted()
//        icon_video_play.visibility = View.VISIBLE
//        video_loader.pause()
//
//        val mediaMetadataRetriever = MediaMetadataRetriever()
//        mediaMetadataRetriever.setDataSource(context, mSrc)
//        val METADATA_KEY_DURATION =
//            java.lang.Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
//
//        val file = File(mSrc.path)
//
////        if (mTimeVideo < MIN_TIME_FRAME) {
////            if (METADATA_KEY_DURATION - mEndPosition > MIN_TIME_FRAME - mTimeVideo) mEndPosition += MIN_TIME_FRAME - mTimeVideo
////            else if (mStartPosition > MIN_TIME_FRAME - mTimeVideo) mStartPosition -= MIN_TIME_FRAME - mTimeVideo
////        }
//    }

    private fun onClickVideoPlayPause() {
        if (video_loader.isPlaying) {
            icon_video_play.visibility = View.VISIBLE
            video_loader.pause()
            playerTimer.pauseTimer()
        } else {
            icon_video_play.visibility = View.GONE
            if (mResetSeekBar) {
                mResetSeekBar = false
                video_loader.seekTo(mStartPosition)
                notifyProgressUpdate()
            }
            video_loader.start()
            playerTimer.startTimer()
        }
    }

    private fun onVideoPrepared(mp: MediaPlayer) {
        val videoWidth = mp.videoWidth
        val videoHeight = mp.videoHeight
        val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
        val screenWidth = layout_surface_view.width
        val screenHeight = layout_surface_view.height
        val screenProportion = screenWidth.toFloat() / screenHeight.toFloat()
        val lp = video_loader.layoutParams

        if (videoProportion > screenProportion) {
            lp.width = screenWidth
            lp.height = (screenWidth.toFloat() / videoProportion).toInt()
        } else {
            lp.width = (videoProportion * screenHeight.toFloat()).toInt()
            lp.height = screenHeight
        }
        video_loader.layoutParams = lp

        icon_video_play.visibility = View.VISIBLE

        mDuration = video_loader.duration
        handlerTop.max = mDuration
        mStartPosition = 0
        mEndPosition = mDuration
        mTimeVideo = mEndPosition

        setTimeFrames()

        mOnVideoListener?.onVideoPrepared()

        timeLineBar.setRange(mStartPosition, mEndPosition)
        timeLineBar.selectedMinValue = 0
        timeLineBar.selectedMaxValue = mEndPosition
        timeLineBar.isNotifyWhileDragging = true
    }

    private fun setTimeFrames() {
        textTimeSelection.text = String.format(
            "%s - %s",
            stringForTime(mStartPosition),
            stringForTime(mEndPosition)
        )
    }

    private fun onSeekThumbs(minValue: Int, maxValue: Int) {
        mStartPosition = minValue
        mEndPosition = maxValue

        if (mStartPosition > handlerTop.progress) {
            handlerTop.progress = mStartPosition
        }
        if (mEndPosition < handlerTop.progress) {
            handlerTop.progress = mEndPosition
        }
        setTimeFrames()
        mTimeVideo = mEndPosition - mStartPosition
    }

    private fun onStopSeekThumbs() {
        video_loader.pause()
        icon_video_play.visibility = View.VISIBLE
    }

    private fun onVideoCompleted() {
        video_loader.seekTo(mStartPosition)
        playerTimer.pauseTimer()
    }

    private fun notifyProgressUpdate() {
        if (mDuration == 0) return
        val position = video_loader.currentPosition
        mListener?.updateProgress(position, mDuration, (position * 100 / mDuration))
        updateVideoProgress(position)
    }

    private fun updateVideoProgress(time: Int) {
        if (video_loader == null) return
        if (time >= mEndPosition) {
            video_loader.pause()
            icon_video_play.visibility = View.VISIBLE
            mResetSeekBar = true
            return
        }
        setProgressBarPosition(time)
    }

    fun setVideoInformationVisibility(visible: Boolean): VideoTrimmer {
        timeFrame.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    fun setOnVideoListener(onVideoListener: OnVideoListener): VideoTrimmer {
        mOnVideoListener = onVideoListener
        return this
    }

    fun destroy() {
        BackgroundExecutor.cancelAll("", true)
        UiThreadExecutor.cancelAll("")
    }

    fun setMaxDuration(maxDuration: Int): VideoTrimmer {
        mMaxDuration = maxDuration * 1000
        return this
    }

    fun setMinDuration(minDuration: Int): VideoTrimmer {
        mMinDuration = minDuration * 1000
        return this
    }

    fun setVideoURI(videoURI: Uri): VideoTrimmer {
        mSrc = videoURI
        video_loader.setVideoURI(mSrc)
        video_loader.requestFocus()
        timeLineView.init(mSrc)
        return this
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

    inner class PlayerTimer {
        private var timer: Timer? = null

        fun startTimer() {
            timer = Timer().also {
                it.scheduleAtFixedRate(100, 100) {
                    CoroutineScope(Dispatchers.Main).launch {
                        notifyProgressUpdate()
                    }
                }
            }
        }

        fun pauseTimer() {
            stopTimer()
        }

        private fun stopTimer() {
            timer?.cancel()
            timer?.purge()
            timer = null
        }
    }
}

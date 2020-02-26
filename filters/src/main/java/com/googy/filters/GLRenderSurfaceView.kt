package com.googy.filters

import android.content.Context
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.os.Build
import android.util.AttributeSet
import com.googy.filters.gpuimage.glfilters.GPUImageExtTexFilter
import com.googy.filters.gpuimage.glfilters.GPUImageFilter
import com.googy.filters.gpuimage.glfilters.GPUImageFilterGroup
import com.googy.filters.gpuimage.glfilters.GPUImageRenderer
import java.util.*

class GLRenderSurfaceView : GLSurfaceView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private var mInputWidth: Int = 1
    private var mInputHeight: Int = 1
    private var renderer: GPUImageRenderer? = null
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var appliedFilters = GPUImageFilterGroup()
    private var playerProgressListener: PlayerProgressListener? = null
    private val playerTimer: PlayerTimer = PlayerTimer()

    fun init(path: String, listener: MediaPlayer.OnPreparedListener) {
        mediaPlayer.setDataSource(path)
        mediaPlayer.isLooping = true
        mediaPlayer.setOnPreparedListener(listener)
        mediaPlayer.prepareAsync()
        setEGLContextClientVersion(2)
        appliedFilters.addFilter(GPUImageExtTexFilter())
        renderer = GPUImageRenderer(appliedFilters)

        holder.setFormat(PixelFormat.RGBA_8888)
        renderer?.setUpSurfaceTexture(mediaPlayer)
        setRenderer(renderer)
    }

    fun onPrepared(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
        setSourceSize(mediaPlayer.videoWidth, mediaPlayer.videoHeight)
    }

    fun setPlayerProgressLisnener(playerProgressListener: PlayerProgressListener) {
        this.playerProgressListener = playerProgressListener
    }

    private fun setSourceSize(inputWidth: Int, inputHeight: Int) {
        mInputWidth = inputWidth
        mInputHeight = inputHeight
        renderer?.setSourceSize(inputWidth, inputHeight)
        playerProgressListener?.let { playerTimer.startTimer() }
    }

    fun setScaledSize(widthScale: Float, heightScale: Float) {
        mInputWidth = (mInputWidth * widthScale).toInt()
        mInputHeight = (mInputHeight * heightScale).toInt()
        renderer?.setSourceSize(mInputWidth, mInputHeight)
    }

    fun playPause() {
        if (mediaPlayer.isPlaying) pause() else resume()
    }

    fun pause() {
        mediaPlayer.pause()
        playerTimer.pauseTimer()
    }

    fun resume() {
        mediaPlayer.start()
        playerTimer.startTimer()
    }

    fun destroy() {
        playerTimer.pauseTimer()
        mediaPlayer.reset()
        mediaPlayer.release()
        playerProgressListener = null
    }

    fun seek(time: Int) {
        mediaPlayer.seekTo(time)
    }

    fun previewFilter(gpuImageFilter: GPUImageFilter) {
        val previewFilterGroup = GPUImageFilterGroup()
        previewFilterGroup.addFilters(appliedFilters.filters)
        previewFilterGroup.addFilter(gpuImageFilter)
        renderer?.setFilter(previewFilterGroup)
    }

    fun resetToAppliedFilters() {
        renderer?.setFilter(appliedFilters)
    }

    fun applyFilter(gpuImageFilter: GPUImageFilter) {
        appliedFilters.addFilter(gpuImageFilter)
        renderer?.setFilter(appliedFilters)
    }

    fun applyFilters(gpuImageFilter: MutableList<GPUImageFilter>) {
        if (gpuImageFilter.isNotEmpty()) {
            appliedFilters.addFilters(gpuImageFilter)
            renderer?.setFilter(appliedFilters)
        }
    }

    fun resetFilters() {
        appliedFilters = GPUImageFilterGroup()
        appliedFilters.addFilter(GPUImageExtTexFilter())
        renderer?.setFilter(appliedFilters)
    }

    fun videoWidth() = mInputWidth
    fun videoHeight() = mInputHeight

    fun getAppliedFilters() = appliedFilters.filters.takeLast(appliedFilters.filters.size - 1)

    fun setSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer.pause()
            mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
            mediaPlayer.start()
        }
    }

    fun currentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun isPlaying() = mediaPlayer.isPlaying

    interface PlayerProgressListener {
        fun onPlayerProgressChanged(time: Int)
    }

    inner class PlayerTimer {
        private var timer: Timer? = null
        private var timerTask: TimerTask? = null

        fun startTimer() {
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    playerProgressListener?.onPlayerProgressChanged(mediaPlayer.currentPosition)
                }
            }
            timer?.scheduleAtFixedRate(timerTask, 16, 16)
        }

        fun pauseTimer() {
            stopTimer()
        }

        private fun stopTimer() {
            timer?.cancel()
            timer?.purge()
            timerTask?.cancel()
            timerTask = null
            timer = null
        }
    }
}

package com.googy.videoeditor.ui.player

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.C.TIME_UNSET
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import com.googy.videoeditor.ui.extension.*

class VideoPlayer(private val context: Context?) : Player.EventListener, TimeBar.OnScrubListener,
    VideoListener {

    private val player: SimpleExoPlayer by lazy { ExoPlayerFactory.newSimpleInstance(context) }
    private var mUpdateListener: OnProgressUpdateListener? = null
    private val progressHandler: Handler = Handler()

    private val progressUpdater: Runnable = Runnable {
        updateProgress()
        initUpdateTimer()
    }

    private val mediaRetriever by lazy { MediaMetadataRetriever() }

    fun isPlaying() = player.isPlaying()

    fun getVideoPlayer() = player

    fun initMediaSource(context: Context?, uri: String) {

        val dataSourceFactory =
            DefaultDataSourceFactory(context, Util.getUserAgent(context, "ExoPlayer"))
        val videoSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(uri))

        player.repeatMode = Player.REPEAT_MODE_ALL
        player.addListener(this)
        player.addVideoListener(this)
        player.prepare(videoSource)
        mediaRetriever.setDataSource(uri)
    }

    fun getVideoHeight(): Int {
        return mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            .toInt()
    }

    fun getVideoWidth(): Int {
        return mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            .toInt()
    }

    fun getVideoRotation(): Int {
        return mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            .toInt()
    }

    fun start() {
        player.start()
        initUpdateTimer()
    }

    fun play() {
        initUpdateTimer()
        player.resume()
    }

    fun pause() {
        player.pause()
        removeUpdater()
    }

    fun stop() {
        player.stop()
        removeUpdater()
    }

    fun release() {
        stop()
        player.release()
        removeUpdater()
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        updateProgress()
    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray?,
        trackSelections: TrackSelectionArray?
    ) {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (isPlaying()) initUpdateTimer()
        else removeUpdater()
        updateProgress()
    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onPlayerError(error: ExoPlaybackException?) {

    }

    override fun onPositionDiscontinuity(reason: Int) {
        updateProgress()
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

    }

    override fun onSeekProcessed() {
        updateProgress()
    }

    override fun onScrubStart(timeBar: TimeBar, position: Long) {

    }

    override fun onScrubMove(timeBar: TimeBar, position: Long) {
        seekTo(position)
        updateProgress()
    }

    override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
        seekTo(position)
        updateProgress()
    }

    private fun updateProgress() {
        mUpdateListener?.onProgressUpdate(
            player.currentPosition,
            if (player.duration == TIME_UNSET) 0L else player.duration,
            player.bufferedPosition
        )
    }

    private fun initUpdateTimer() {
        val position = player.currentPosition
        var delayMs: Long
        if (player.isVideoAvailable()) {
            if (player.isPlaying()) {
                delayMs = 1000 - position % 1000
                if (delayMs < 200) {
                    delayMs += 1000
                }
            } else {
                delayMs = 1000
            }
            progressHandler.postDelayed(progressUpdater, delayMs)
        }
    }

    private fun removeUpdater() {
        progressHandler.removeCallbacks(progressUpdater)
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun setUpdateListener(updateListener: OnProgressUpdateListener) {
        mUpdateListener = updateListener
    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        mUpdateListener?.onFirstTimeUpdate(
            player.duration,
            player.currentPosition,
            width,
            height,
            unappliedRotationDegrees,
            pixelWidthHeightRatio
        )
    }

    override fun onRenderedFirstFrame() {

    }

    interface OnProgressUpdateListener {
        fun onProgressUpdate(currentPosition: Long, duration: Long, bufferedPosition: Long)

        fun onFirstTimeUpdate(
            duration: Long,
            currentPosition: Long,
            width: Int,
            height: Int,
            unappliedRotationDegrees: Int,
            pixelWidthHeightRatio: Float
        )
    }
}
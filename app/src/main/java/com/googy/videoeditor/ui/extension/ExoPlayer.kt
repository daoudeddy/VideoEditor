package com.googy.videoeditor.ui.extension

import android.text.format.DateUtils
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.SimpleExoPlayer

fun SimpleExoPlayer.currentProgress(): String {
    return DateUtils.formatElapsedTime(currentPosition / 1000)
}

fun SimpleExoPlayer.videoDuration(): String {
    return DateUtils.formatElapsedTime(duration / 1000)
}

fun SimpleExoPlayer.isPlaying(): Boolean {
    return playbackState == STATE_READY && playWhenReady
}

fun SimpleExoPlayer.isVideoAvailable(): Boolean {
    return playbackState == STATE_READY || playbackState == STATE_BUFFERING
}

fun SimpleExoPlayer.pause() {
    playWhenReady = false
}

fun SimpleExoPlayer.resume() {
    start()
}

fun SimpleExoPlayer.start() {
    if (playbackError != null || playbackState == STATE_IDLE || playbackState == STATE_ENDED) retry()
    else if (!playWhenReady) playWhenReady = true
}
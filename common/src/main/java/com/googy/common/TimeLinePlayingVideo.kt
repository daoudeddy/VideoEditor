package com.googy.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import kotlinx.coroutines.*

class TimeLinePlayingVideo constructor(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    companion object {
        private const val RATIO_THUMBNAILS = 4.7
        private const val UNIT_DURATIONS = 30f
        private const val MIN_THUMBNAILS = 5
    }

    private var thumbWidth = 0
    private var thumbHeight = 0
    private var numThumbnails = 0
    private var widthScreen = 0
    private var listThumbnails = mutableListOf<Bitmap>()
    private var job: Job? = null
    private var loadInfoViewListener: ((Float, Int) -> Unit)? = null
    private var durationVideo = 0L
    private var path: String = ""

    init {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        widthScreen = displayMetrics.widthPixels
        thumbWidth = (widthScreen.toFloat() / RATIO_THUMBNAILS).toInt()
        thumbHeight = resources.getDimension(R.dimen.playing_video_frames_video_height).toInt()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    fun initTimeLine(path: String) {
        if (this.path != path) {
            this.path = path
            listThumbnails.clear()
            invalidate()

            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(
                context,
                Uri.parse(path)
            )

            durationVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()

            numThumbnails = getAmountThumbnails(durationVideo)

            val newLayoutParams = layoutParams
            newLayoutParams.width = thumbWidth * numThumbnails + widthScreen
            layoutParams = newLayoutParams

            loadInfoViewListener?.invoke(durationVideo.toFloat(), thumbWidth * numThumbnails)
            job = CoroutineScope(Dispatchers.IO).launch {
                listThumbnails = getListThumbnails(retriever)
                withContext(Dispatchers.Main) {
                    invalidate()
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (listThumbnails.isNotEmpty()) {
            var x = widthScreen / 2f
            for (i in 0 until listThumbnails.size) {
                val bitmap = listThumbnails.getOrNull(i)
                bitmap?.let {
                    canvas.drawBitmap(bitmap, x, 0f, null)
                    x += bitmap.width
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job?.cancel()
    }

    fun setLoadInfoVideoListener(listener: (durationVideo: Float, widthThumbnails: Int) -> Unit) {
        loadInfoViewListener = listener
    }

    private fun getListThumbnails(
        retriever: MediaMetadataRetriever
    ): MutableList<Bitmap> {
        val bitmapList = mutableListOf<Bitmap>()
        try {
            val interval = durationVideo * 1000 / numThumbnails

            (0 until numThumbnails).forEach { index ->
                val bitmap = retriever.getFrameAtTime(
                    index * interval,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
                val croppedBitmap = if (bitmap.height > bitmap.width) {
                    Bitmap.createBitmap(
                        bitmap,
                        0,
                        bitmap.height / 2 - bitmap.width / 2,
                        bitmap.width,
                        bitmap.width
                    )
                } else {
                    Bitmap.createBitmap(
                        bitmap,
                        bitmap.width / 2 - bitmap.height / 2,
                        0,
                        bitmap.height,
                        bitmap.height
                    )
                }
                try {
                    val scaledBitmap =
                        Bitmap.createScaledBitmap(croppedBitmap, thumbWidth, thumbHeight, false)
                    bitmapList.add(scaledBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    bitmap.recycle()
                    croppedBitmap.recycle()
                }
            }
            retriever.release()
        } catch (e: Throwable) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e)
        }
        return bitmapList
    }

    private fun getAmountThumbnails(durationVideo: Long): Int {
        val durationSeconds = (durationVideo / 1000)
        return MIN_THUMBNAILS + Math.round(durationSeconds / UNIT_DURATIONS)
    }
}
package com.googy.videoeditor.ui.base

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import com.googy.videoeditor.R
import com.googy.videoeditor.ui.extension.dpToPx
import com.googy.videoeditor.ui.extension.runInBackground
import com.googy.videoeditor.ui.extension.runOnUi
import com.googy.videoeditor.ui.extension.runWithUi
import kotlinx.android.synthetic.main.timeline_view.view.*
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.M)
class TimeLineView constructor(
    context: Context,
    attrs: AttributeSet
) : FrameLayout(context, attrs), View.OnTouchListener, View.OnScrollChangeListener {

    private var isSeeking: Boolean = false
    private var videoDuration: Long = 0
    private var seekBarChangeListener: OnSeekBarChangeListener? = null

    init {
        View.inflate(context, R.layout.timeline_view, this)
    }

    fun initTimeLineView(path: String, onSeekBarChangeListener: OnSeekBarChangeListener) {
        timeline.setPadding(width / 2, 0, width / 2, 0)
        scrollView.setOnTouchListener(this)
        seekBarChangeListener = onSeekBarChangeListener
        runInBackground { getListThumbnails(path) }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                seekBarChangeListener?.onStartTrackingTouch()
                scrollView.setOnScrollChangeListener(this)
                isSeeking = true
                true
            }
            MotionEvent.ACTION_UP -> {
                seekBarChangeListener?.onStopTrackingTouch()
                scrollView.setOnScrollChangeListener(null)
                isSeeking = false
                true
            }
            else -> false
        }
    }

    override fun onScrollChange(
        v: View?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if (timeline.width != 0) {
            val duration = scrollView.scrollX.toLong() * videoDuration / timeline.width
            seekBarChangeListener?.onProgressChanged(duration.toInt())
        }
    }

    fun scrollTo(duration: Int) {
        if (!isSeeking) {
            runOnUi {
                val scrollX = (timeline.width.toLong() * duration.toLong() / videoDuration)
                if (scrollX > scrollView.scrollX || scrollX < 1000) {
                    scrollView.scrollTo(scrollX.toInt(), 0)
                }
            }
        }
    }

    private suspend fun getListThumbnails(path: String) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        try {
            videoDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
            val numThumbnails = getAmountThumbnails(videoDuration)

            val interval = videoDuration * 1000 / numThumbnails

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
                    val scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, 85, 85, false)
                    runWithUi {
                        timeline.addView(AppCompatImageView(context).apply {
                            layoutParams = LayoutParams(dpToPx(80), dpToPx(80))
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            setImageBitmap(scaledBitmap)
                        })
                    }
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
    }

    private fun getAmountThumbnails(durationVideo: Long): Int {
        val durationSeconds = (durationVideo / 1000)
        return MIN_THUMBNAILS + (durationSeconds / UNIT_DURATIONS).roundToInt()
    }

    companion object {
        private const val UNIT_DURATIONS = 15f
        private const val MIN_THUMBNAILS = 5
    }

    interface OnSeekBarChangeListener {
        fun onProgressChanged(progress: Int)
        fun onStartTrackingTouch()
        fun onStopTrackingTouch()
    }
}
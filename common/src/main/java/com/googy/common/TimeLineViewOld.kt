package com.googy.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC
import android.net.Uri
import android.util.AttributeSet
import android.util.LongSparseArray
import android.view.View
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import kotlinx.coroutines.*
import kotlin.math.ceil

class TimeLineViewOld @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var job: Job? = null
    private var videoUri: Uri? = null
    private val viewHeight: Int by lazy { context.resources.getDimensionPixelOffset(R.dimen.frames_video_height) }
    private var scaledBitmaps: LongSparseArray<Bitmap> = LongSparseArray()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW = paddingLeft + paddingRight + suggestedMinimumWidth
        val w = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val minH = paddingBottom + paddingTop + viewHeight
        val h = resolveSizeAndState(minH, heightMeasureSpec, 1)
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        if (w != oldW && videoUri != null) {
            job = CoroutineScope(Dispatchers.IO).launch { getBitmap(w) }
        }
    }

    private suspend fun getBitmap(viewWidth: Int) {
        videoUri?.let {
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(context, it)
                val lengthInMs = extractMetadata(METADATA_KEY_DURATION).toLong() * 1000
                val thumbWidth = viewHeight / 2
                val thumbHeight = viewHeight
                val numThumbs = ceil((viewWidth.toFloat() / thumbWidth).toDouble()).toInt()
                val interval = lengthInMs / numThumbs
                for (i in 0 until numThumbs) {
                    getFrameAtTime(i * interval, OPTION_CLOSEST_SYNC)?.let {
                        val bitmap = Bitmap.createScaledBitmap(
                            it,
                            thumbWidth,
                            thumbHeight,
                            false
                        )
                        scaledBitmaps.put(i * interval, bitmap)
                    }
                }
                withContext(Dispatchers.Main) {
                    invalidate()
                }
            }
            retriever.release()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (scaledBitmaps.isNotEmpty()) {
            canvas.save()
            var x = 0
            scaledBitmaps.forEach { _, value ->
                canvas.drawBitmap(value, x.toFloat(), 0f, null)
                x += value.width
            }
        }
    }

    fun destroy() {
        job?.cancel()
        scaledBitmaps.forEach { key, value -> value.recycle() }
        scaledBitmaps.clear()
    }

    fun init(uri: Uri) {
        videoUri = uri
        if (width > 0) {
            job = CoroutineScope(Dispatchers.IO).launch { getBitmap(width) }
        }
    }

    fun init(path: String) {
        init(Uri.parse(path))
    }

}

package com.googy.videoeditor.ui.crop.cropview.window

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Pair
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

import com.googy.videoeditor.ui.crop.cropview.util.AspectRatioUtil
import com.googy.videoeditor.ui.crop.cropview.util.HandleUtil
import com.googy.videoeditor.ui.crop.cropview.util.PaintUtil
import com.googy.videoeditor.ui.crop.cropview.window.edge.Edge
import com.googy.videoeditor.ui.crop.cropview.window.handle.Handle
import kotlin.math.abs
import kotlin.math.max

class CropView : View {
    private val mBorderPaint: Paint by lazy { PaintUtil.newBorderPaint(context) }
    private val mGuidelinePaint: Paint by lazy { PaintUtil.newGuidelinePaint() }
    private val mCornerPaint: Paint by lazy { PaintUtil.newCornerPaint(context) }
    private val mBackgroundPaint: Paint by lazy { PaintUtil.newBackgroundPaint() }
    private val displayMetrics: DisplayMetrics by lazy { context.resources.displayMetrics }
    private val mHandleRadius: Float by lazy { HandleUtil.getTargetRadius(context) }
    private val mSnapRadius: Float by lazy { TypedValue.applyDimension(1, 6.0f, displayMetrics) }
    private val mCornerOffset: Float by lazy { TypedValue.applyDimension(1, DEFAULT_CORNER_OFFSET_DP, displayMetrics) }
    private val mCornerExtension: Float by lazy { TypedValue.applyDimension(1, DEFAULT_CORNER_EXTENSION_DP, displayMetrics) }
    private val mCornerLength: Float by lazy { TypedValue.applyDimension(1, 20.0f, displayMetrics) }

    private var mBitmapRect: Rect? = null
    private var mTouchOffset: Pair<Float, Float>? = null

    private var mPressedHandle: Handle? = null

    private var initializedCropWindow: Boolean = false
    private var mFixAspectRatio = false
    private var mAspectRatioX = 1
    private var mAspectRatioY = 1
    private var mTargetAspectRatio: Float = 0f
    private var mGuidelines: Int = 1

    constructor(context: Context) : super(context) {
        mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
        initializedCropWindow = false
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
        initializedCropWindow = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        initCropWindow(mBitmapRect)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas, mBitmapRect)
        if (showGuidelines()) {
            if (mGuidelines == 2) {
                drawRuleOfThirdsGuidelines(canvas)
            } else if (mGuidelines == 1) {
                if (mPressedHandle != null) {
                    drawRuleOfThirdsGuidelines(canvas)
                }
            } else if (mGuidelines == 0) {
            }
        }

        canvas.drawRect(Edge.LEFT.coordinate, Edge.TOP.coordinate, Edge.RIGHT.coordinate, Edge.BOTTOM.coordinate, mBorderPaint)
        drawCorners(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (!isEnabled) {
            false
        } else {
            when (event.action) {
                0 -> {
                    onActionDown(event.x, event.y)
                    true
                }
                1, 3 -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    onActionUp()
                    true
                }
                2 -> {
                    onActionMove(event.x, event.y)
                    parent.requestDisallowInterceptTouchEvent(true)
                    true
                }
                else -> false
            }
        }
    }

    fun setBitmapRect(bitmapRect: Rect) {
        mBitmapRect = bitmapRect
        initCropWindow(mBitmapRect)
    }

    fun resetCropOverlayView() {
        if (initializedCropWindow) {
            initCropWindow(mBitmapRect)
            invalidate()
        }
    }

    fun setGuidelines(guidelines: Int) {
        if (guidelines in 0..2) {
            mGuidelines = guidelines
            if (initializedCropWindow) {
                initCropWindow(mBitmapRect)
                invalidate()
            }

        } else {
            throw IllegalArgumentException("Guideline value must be set between 0 and 2. See documentation.")
        }
    }

    fun setFixedAspectRatio(fixAspectRatio: Boolean) {
        mFixAspectRatio = fixAspectRatio
        if (initializedCropWindow) {
            initCropWindow(mBitmapRect)
            invalidate()
        }
    }

    fun setAspectRatioX(aspectRatioX: Int) {
        if (aspectRatioX <= 0) {
            throw IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.")
        } else {
            mAspectRatioX = aspectRatioX
            mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
            if (initializedCropWindow) {
                initCropWindow(mBitmapRect)
                invalidate()
            }

        }
    }

    fun setAspectRatioY(aspectRatioY: Int) {
        if (aspectRatioY <= 0) {
            throw IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.")
        } else {
            mAspectRatioY = aspectRatioY
            mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
            if (initializedCropWindow) {
                initCropWindow(mBitmapRect)
                invalidate()
            }

        }
    }

    fun setInitialAttributeValues(guidelines: Int, fixAspectRatio: Boolean, aspectRatioX: Int, aspectRatioY: Int) {
        if (guidelines in 0..2) {
            mGuidelines = guidelines
            mFixAspectRatio = fixAspectRatio
            if (aspectRatioX <= 0) {
                throw IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.")
            } else {
                mAspectRatioX = aspectRatioX
                mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
                if (aspectRatioY <= 0) {
                    throw IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.")
                } else {
                    mAspectRatioY = aspectRatioY
                    mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY.toFloat()
                }
            }
        } else {
            throw IllegalArgumentException("Guideline value must be set between 0 and 2. See documentation.")
        }
    }

    private fun initCropWindow(bitmapRect: Rect?) {
        if (!initializedCropWindow) {
            initializedCropWindow = true
        }

        val centerX: Float
        val cropWidth: Float
        if (mFixAspectRatio) {
            val halfCropWidth: Float
            if (AspectRatioUtil.calculateAspectRatio(bitmapRect!!) > mTargetAspectRatio) {
                Edge.TOP.coordinate = bitmapRect.top.toFloat()
                Edge.BOTTOM.coordinate = bitmapRect.bottom.toFloat()
                centerX = width.toFloat() / 2.0f
                cropWidth = max(40.0f, AspectRatioUtil.calculateWidth(Edge.TOP.coordinate, Edge.BOTTOM.coordinate, mTargetAspectRatio))
                if (cropWidth == 40.0f) {
                    mTargetAspectRatio = 40.0f / (Edge.BOTTOM.coordinate - Edge.TOP.coordinate)
                }

                halfCropWidth = cropWidth / 2.0f
                Edge.LEFT.coordinate = centerX - halfCropWidth
                Edge.RIGHT.coordinate = centerX + halfCropWidth
            } else {
                Edge.LEFT.coordinate = bitmapRect.left.toFloat()
                Edge.RIGHT.coordinate = bitmapRect.right.toFloat()
                centerX = height.toFloat() / 2.0f
                cropWidth = max(40.0f, AspectRatioUtil.calculateHeight(Edge.LEFT.coordinate, Edge.RIGHT.coordinate, mTargetAspectRatio))
                if (cropWidth == 40.0f) {
                    mTargetAspectRatio = (Edge.RIGHT.coordinate - Edge.LEFT.coordinate) / 40.0f
                }

                halfCropWidth = cropWidth / 2.0f
                Edge.TOP.coordinate = centerX - halfCropWidth
                Edge.BOTTOM.coordinate = centerX + halfCropWidth
            }
        } else {
            centerX = 0.1f * bitmapRect!!.width().toFloat()
            cropWidth = 0.1f * bitmapRect.height().toFloat()
            Edge.LEFT.coordinate = bitmapRect.left.toFloat() + centerX
            Edge.TOP.coordinate = bitmapRect.top.toFloat() + cropWidth
            Edge.RIGHT.coordinate = bitmapRect.right.toFloat() - centerX
            Edge.BOTTOM.coordinate = bitmapRect.bottom.toFloat() - cropWidth
        }
    }

    private fun drawRuleOfThirdsGuidelines(canvas: Canvas) {
        val left = Edge.LEFT.coordinate
        val top = Edge.TOP.coordinate
        val right = Edge.RIGHT.coordinate
        val bottom = Edge.BOTTOM.coordinate
        val oneThirdCropWidth = Edge.width / 3.0f
        val x1 = left + oneThirdCropWidth
        canvas.drawLine(x1, top, x1, bottom, mGuidelinePaint)
        val x2 = right - oneThirdCropWidth
        canvas.drawLine(x2, top, x2, bottom, mGuidelinePaint)
        val oneThirdCropHeight = Edge.height / 3.0f
        val y1 = top + oneThirdCropHeight
        canvas.drawLine(left, y1, right, y1, mGuidelinePaint)
        val y2 = bottom - oneThirdCropHeight
        canvas.drawLine(left, y2, right, y2, mGuidelinePaint)
    }

    private fun drawBackground(canvas: Canvas, bitmapRect: Rect?) {
        bitmapRect?.let {
            val left = Edge.LEFT.coordinate
            val top = Edge.TOP.coordinate
            val right = Edge.RIGHT.coordinate
            val bottom = Edge.BOTTOM.coordinate
            canvas.drawRect(it.left.toFloat(), it.top.toFloat(), it.right.toFloat(), top, mBackgroundPaint)
            canvas.drawRect(it.left.toFloat(), bottom, it.right.toFloat(), it.bottom.toFloat(), mBackgroundPaint)
            canvas.drawRect(it.left.toFloat(), top, left, bottom, mBackgroundPaint)
            canvas.drawRect(right, top, it.right.toFloat(), bottom, mBackgroundPaint)
        }
    }

    private fun drawCorners(canvas: Canvas) {
        val left = Edge.LEFT.coordinate
        val top = Edge.TOP.coordinate
        val right = Edge.RIGHT.coordinate
        val bottom = Edge.BOTTOM.coordinate
        canvas.drawLine(left - mCornerOffset, top - mCornerExtension, left - mCornerOffset, top + mCornerLength, mCornerPaint)
        canvas.drawLine(left, top - mCornerOffset, left + mCornerLength, top - mCornerOffset, mCornerPaint)
        canvas.drawLine(right + mCornerOffset, top - mCornerExtension, right + mCornerOffset, top + mCornerLength, mCornerPaint)
        canvas.drawLine(right, top - mCornerOffset, right - mCornerLength, top - mCornerOffset, mCornerPaint)
        canvas.drawLine(left - mCornerOffset, bottom + mCornerExtension, left - mCornerOffset, bottom - mCornerLength, mCornerPaint)
        canvas.drawLine(left, bottom + mCornerOffset, left + mCornerLength, bottom + mCornerOffset, mCornerPaint)
        canvas.drawLine(right + mCornerOffset, bottom + mCornerExtension, right + mCornerOffset, bottom - mCornerLength, mCornerPaint)
        canvas.drawLine(right, bottom + mCornerOffset, right - mCornerLength, bottom + mCornerOffset, mCornerPaint)
    }

    private fun onActionDown(x: Float, y: Float) {
        val left = Edge.LEFT.coordinate
        val top = Edge.TOP.coordinate
        val right = Edge.RIGHT.coordinate
        val bottom = Edge.BOTTOM.coordinate
        mPressedHandle = HandleUtil.getPressedHandle(x, y, left, top, right, bottom, mHandleRadius)
        if (mPressedHandle != null) {
            mTouchOffset = HandleUtil.getOffset(mPressedHandle, x, y, left, top, right, bottom)
            invalidate()
        }
    }

    private fun onActionUp() {
        if (mPressedHandle != null) {
            mPressedHandle = null
            invalidate()
        }
    }

    private fun onActionMove(x: Float, y: Float) {
        var tempX = x
        var tempY = y
        if (mPressedHandle != null) {
            tempX += mTouchOffset?.first ?: 0f
            tempY += mTouchOffset?.second ?: 0f
            if (mFixAspectRatio) {
                mPressedHandle!!.updateCropWindow(tempX, tempY, mTargetAspectRatio, mBitmapRect!!, mSnapRadius)
            } else {
                mPressedHandle!!.updateCropWindow(tempX, tempY, mBitmapRect!!, mSnapRadius)
            }
            invalidate()
        }
    }

    companion object {
        private val SNAP_RADIUS_DP = 6
        private val DEFAULT_SHOW_GUIDELINES_LIMIT = 100.0f
        private val DEFAULT_CORNER_THICKNESS_DP = PaintUtil.cornerThickness
        private val DEFAULT_LINE_THICKNESS_DP = PaintUtil.lineThickness
        private val DEFAULT_CORNER_OFFSET_DP: Float
        private val DEFAULT_CORNER_EXTENSION_DP: Float
        private val DEFAULT_CORNER_LENGTH_DP = 20.0f
        private val GUIDELINES_OFF = 0
        private val GUIDELINES_ON_TOUCH = 1
        private val GUIDELINES_ON = 2

        fun showGuidelines(): Boolean {
            return abs(Edge.LEFT.coordinate - Edge.RIGHT.coordinate) >= 100.0f && abs(Edge.TOP.coordinate - Edge.BOTTOM.coordinate) >= 100.0f
        }

        init {
            DEFAULT_CORNER_OFFSET_DP = DEFAULT_CORNER_THICKNESS_DP / 2.0f - DEFAULT_LINE_THICKNESS_DP / 2.0f
            DEFAULT_CORNER_EXTENSION_DP = DEFAULT_CORNER_THICKNESS_DP / 2.0f + DEFAULT_CORNER_OFFSET_DP
        }
    }
}

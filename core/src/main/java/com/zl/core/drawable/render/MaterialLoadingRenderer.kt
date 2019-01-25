package com.zl.core.drawable.render

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.RectF
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.zl.core.utils.DisplayUtils

class MaterialLoadingRenderer(context: Context) : LoadingRenderer(context) {

    private val mPaint = Paint()
    private val mTempBounds = RectF()

    private val mAnimatorListener = object : AnimatorListenerAdapter() {
        override fun onAnimationRepeat(animator: Animator) {
            super.onAnimationRepeat(animator)
            storeOriginals()
            goToNextColor()

            mStartDegrees = mEndDegrees
            mRotationCount = (mRotationCount + 1) % NUM_POINTS
        }

        override fun onAnimationStart(animation: Animator) {
            super.onAnimationStart(animation)
            mRotationCount = 0f
        }
    }

    private var mColors: IntArray? = null
    private var mColorIndex: Int = 0
    private var mCurrentColor: Int = 0

    private var mStrokeInset: Float = 0.toFloat()

    private var mRotationCount: Float = 0.toFloat()
    private var mGroupRotation: Float = 0.toFloat()

    private var mEndDegrees: Float = 0.toFloat()
    private var mStartDegrees: Float = 0.toFloat()
    private var mSwipeDegrees: Float = 0.toFloat()
    private var mOriginEndDegrees: Float = 0.toFloat()
    private var mOriginStartDegrees: Float = 0.toFloat()

    private var mStrokeWidth: Float = 0.toFloat()
    private var mCenterRadius: Float = 0.toFloat()

    private val nextColor: Int
        get() = mColors!![nextColorIndex]

    private val nextColorIndex: Int
        get() = (mColorIndex + 1) % mColors!!.size

    private val startingColor: Int
        get() = mColors!![mColorIndex]

    init {
        init(context)
        setupPaint()
        addRenderListener(mAnimatorListener)
    }

    private fun init(context: Context) {
        mStrokeWidth = DisplayUtils.dp2px(context, DEFAULT_STROKE_WIDTH)
        mCenterRadius = DisplayUtils.dp2px(context, DEFAULT_CENTER_RADIUS)

        mColors = DEFAULT_COLORS

        setColorIndex(0)
        initStrokeInset(mWidth, mHeight)
    }

    private fun setupPaint() {
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = mStrokeWidth
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun draw(canvas: Canvas) {
        val saveCount = canvas.save()

        mTempBounds.set(mBounds)
        mTempBounds.inset(mStrokeInset, mStrokeInset)

        canvas.rotate(mGroupRotation, mTempBounds.centerX(), mTempBounds.centerY())

        if (mSwipeDegrees != 0f) {
            mPaint.color = mCurrentColor
            canvas.drawArc(mTempBounds, mStartDegrees, mSwipeDegrees, false, mPaint)
        }

        canvas.restoreToCount(saveCount)
    }

    override fun computeRender(renderProgress: Float) {
        updateRingColor(renderProgress)

        // Moving the start trim only occurs in the first 50% of a single ring animation
        if (renderProgress <= START_TRIM_DURATION_OFFSET) {
            val startTrimProgress = renderProgress / START_TRIM_DURATION_OFFSET
            mStartDegrees = mOriginStartDegrees + MAX_SWIPE_DEGREES * MATERIAL_INTERPOLATOR.getInterpolation(startTrimProgress)
        }

        // Moving the end trim starts after 50% of a single ring animation completes
        if (renderProgress > START_TRIM_DURATION_OFFSET) {
            val endTrimProgress = (renderProgress - START_TRIM_DURATION_OFFSET) / (END_TRIM_DURATION_OFFSET - START_TRIM_DURATION_OFFSET)
            mEndDegrees = mOriginEndDegrees + MAX_SWIPE_DEGREES * MATERIAL_INTERPOLATOR.getInterpolation(endTrimProgress)
        }

        if (Math.abs(mEndDegrees - mStartDegrees) > 0) {
            mSwipeDegrees = mEndDegrees - mStartDegrees
        }

        mGroupRotation = FULL_GROUP_ROTATION / NUM_POINTS * renderProgress + FULL_GROUP_ROTATION * (mRotationCount / NUM_POINTS)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter) {
        mPaint.colorFilter = cf
    }

    override fun reset() {
        resetOriginals()
    }

    private fun setColorIndex(index: Int) {
        mColorIndex = index
        mCurrentColor = mColors!![mColorIndex]
    }

    private fun goToNextColor() {
        setColorIndex(nextColorIndex)
    }

    private fun initStrokeInset(width: Float, height: Float) {
        val minSize = Math.min(width, height)
        val strokeInset = minSize / 2.0f - mCenterRadius
        val minStrokeInset = Math.ceil((mStrokeWidth / 2.0f).toDouble()).toFloat()
        mStrokeInset = if (strokeInset < minStrokeInset) minStrokeInset else strokeInset
    }

    private fun storeOriginals() {
        mOriginEndDegrees = mEndDegrees
        mOriginStartDegrees = mEndDegrees
    }

    private fun resetOriginals() {
        mOriginEndDegrees = 0f
        mOriginStartDegrees = 0f

        mEndDegrees = 0f
        mStartDegrees = 0f
    }

    private fun updateRingColor(interpolatedTime: Float) {
        if (interpolatedTime > COLOR_START_DELAY_OFFSET) {
            mCurrentColor = evaluateColorChange((interpolatedTime - COLOR_START_DELAY_OFFSET) / (1.0f - COLOR_START_DELAY_OFFSET), startingColor, nextColor)
        }
    }

    private fun evaluateColorChange(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff

        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff

        return (startA + (fraction * (endA - startA)).toInt() shl 24
                or (startR + (fraction * (endR - startR)).toInt() shl 16)
                or (startG + (fraction * (endG - startG)).toInt() shl 8)
                or startB + (fraction * (endB - startB)).toInt())
    }

    private fun apply(builder: Builder) {
        this.mWidth = if (builder.mWidth > 0) builder.mWidth.toFloat() else this.mWidth
        this.mHeight = if (builder.mHeight > 0) builder.mHeight.toFloat() else this.mHeight
        this.mStrokeWidth = if (builder.mStrokeWidth > 0) builder.mStrokeWidth.toFloat() else this.mStrokeWidth
        this.mCenterRadius = if (builder.mCenterRadius > 0) builder.mCenterRadius.toFloat() else this.mCenterRadius

        this.mDuration = if (builder.mDuration > 0) builder.mDuration.toLong() else this.mDuration

        this.mColors = if (builder.mColors != null && builder.mColors!!.size > 0) builder.mColors else this.mColors

        setColorIndex(0)
        setupPaint()
        initStrokeInset(this.mWidth, this.mHeight)
    }

    class Builder(private val mContext: Context) {

        var mWidth: Int = 0
        var mHeight: Int = 0
        var mStrokeWidth: Int = 0
        var mCenterRadius: Int = 0

        var mDuration: Int = 0

        var mColors: IntArray? = null

        fun setWidth(width: Int): Builder {
            this.mWidth = width
            return this
        }

        fun setHeight(height: Int): Builder {
            this.mHeight = height
            return this
        }

        fun setStrokeWidth(strokeWidth: Int): Builder {
            this.mStrokeWidth = strokeWidth
            return this
        }

        fun setCenterRadius(centerRadius: Int): Builder {
            this.mCenterRadius = centerRadius
            return this
        }

        fun setDuration(duration: Int): Builder {
            this.mDuration = duration
            return this
        }

        fun setColors(colors: IntArray): Builder {
            this.mColors = colors
            return this
        }

        fun build(): MaterialLoadingRenderer {
            val loadingRenderer = MaterialLoadingRenderer(mContext)
            loadingRenderer.apply(this)
            return loadingRenderer
        }
    }

    companion object {
        private val MATERIAL_INTERPOLATOR = FastOutSlowInInterpolator()

        private val DEGREE_360 = 360
        private val NUM_POINTS = 5

        private val MAX_SWIPE_DEGREES = 0.8f * DEGREE_360
        private val FULL_GROUP_ROTATION = 3.0f * DEGREE_360

        private val COLOR_START_DELAY_OFFSET = 0.8f
        private val END_TRIM_DURATION_OFFSET = 1.0f
        private val START_TRIM_DURATION_OFFSET = 0.5f

        private val DEFAULT_CENTER_RADIUS = 12.5f
        private val DEFAULT_STROKE_WIDTH = 2.5f

        private val DEFAULT_COLORS = intArrayOf(Color.RED, Color.GREEN, Color.BLUE)
    }
}

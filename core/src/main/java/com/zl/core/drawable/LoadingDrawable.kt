package com.zl.core.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import com.zl.core.drawable.render.LoadingRenderer

class LoadingDrawable(private val mLoadingRender: LoadingRenderer) : Drawable(), Animatable {

    private val mCallback = object : Drawable.Callback {
        override fun invalidateDrawable(d: Drawable?) {
            invalidateSelf()
        }

        override fun scheduleDrawable(d: Drawable, what: Runnable, `when`: Long) {
            scheduleSelf(what, `when`)
        }

        override fun unscheduleDrawable(d: Drawable, what: Runnable) {
            unscheduleSelf(what)
        }
    }

    init {
        this.mLoadingRender.setCallback(mCallback)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        this.mLoadingRender.setBounds(bounds)
    }

    override fun draw(canvas: Canvas) {
        if (!bounds.isEmpty) {
            this.mLoadingRender.draw(canvas)
        }
    }

    override fun setAlpha(alpha: Int) {
        this.mLoadingRender.setAlpha(alpha)
    }

    override fun setColorFilter(cf: ColorFilter?) {
        this.mLoadingRender.setColorFilter(cf!!)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun start() {
        this.mLoadingRender.start()
    }

    override fun stop() {
        this.mLoadingRender.stop()
    }

    override fun isRunning(): Boolean {
        return this.mLoadingRender.isRunning
    }

    override fun getIntrinsicHeight(): Int {
        return this.mLoadingRender.mHeight.toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return this.mLoadingRender.mWidth.toInt()
    }
}

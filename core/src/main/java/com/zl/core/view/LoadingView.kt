package com.zl.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.zl.core.drawable.LoadingDrawable
import com.zl.core.drawable.render.LoadingRenderer

class LoadingView : ImageView {
    private var mLoadingDrawable: LoadingDrawable? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setLoadingRenderer(loadingRenderer: LoadingRenderer) {
        mLoadingDrawable = LoadingDrawable(loadingRenderer)
        setImageDrawable(mLoadingDrawable)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            startAnimation()
        } else {
            stopAnimation()
        }
    }

    private fun startAnimation() {
        if (mLoadingDrawable != null) {
            mLoadingDrawable!!.start()
        }
    }

    private fun stopAnimation() {
        if (mLoadingDrawable != null) {
            mLoadingDrawable!!.stop()
        }
    }
}

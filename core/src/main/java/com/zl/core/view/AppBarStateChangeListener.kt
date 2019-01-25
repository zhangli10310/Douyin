package com.zl.core.view

import com.google.android.material.appbar.AppBarLayout

abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {

    private var mCurrentState = IDLE

    companion object {
        const val EXPANDED = 1        //展开状态
        const val COLLAPSED = 2       //折叠状态
        const val IDLE = 3            //中间状态
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        when {
            i == 0 -> {
                if (mCurrentState != EXPANDED) {
                    onStateChanged(appBarLayout, EXPANDED)
                }
                mCurrentState = EXPANDED
            }
            Math.abs(i) >= appBarLayout.totalScrollRange -> {
                if (mCurrentState != COLLAPSED) {
                    onStateChanged(appBarLayout, COLLAPSED)
                }
                mCurrentState = COLLAPSED
            }
            else -> {
                if (mCurrentState != IDLE) {
                    onStateChanged(appBarLayout, IDLE)
                }
                mCurrentState = IDLE
            }
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout, state: Int)
}
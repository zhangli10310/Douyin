package com.zl.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager


/**
 *
 *<p></p>
 *
 * Created by zhangli on 2019/2/26 11:51.<br/>
 */
class VertiViewPager : ViewPager {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init()
    }

    private fun init(){
        setPageTransformer(true) { page, position ->
            val height = page.height
            val width = page.width
            when {
                position < -1 -> page.alpha = 0f
                position <= 0 -> {
                    page.translationX = -position * width
                    page.translationY = position * height
                    page.alpha = 1f
                }
                position <= 1 -> {
                    page.translationX = -position * width
                    page.translationY = position * height
                    page.alpha = 1f
                }
                else -> page.alpha = 0f
            }
        }
        overScrollMode = OVER_SCROLL_NEVER

        addOnPageChangeListener(object :SimpleOnPageChangeListener(){

            override fun onPageSelected(position: Int) {
                val parent = parent
                if (parent is SwipeRefreshLayout) {
                    parent.isEnabled = currentItem == 0
                }
            }

        })
    }

    private fun swapTouchEvent(event: MotionEvent): MotionEvent {
        val width = width.toFloat()
        val height = height.toFloat()
        event.setLocation(event.y / height * width, event.x / width * height)
        return event
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(swapTouchEvent(MotionEvent.obtain(event)))
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(swapTouchEvent(MotionEvent.obtain(ev)))
    }


}
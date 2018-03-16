package com.zl.core.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/16 18:05.<br/>
 */
class InterceptTouchViewPager : ViewPager {

    var forbidToLeft: Boolean = false
    var forbidToRight: Boolean = false

    private var xLast: Float = 0f
    private var yLast: Float = 0f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

//    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        return isIntercept?.invoke(ev) ?: true && super.onTouchEvent(ev)
//    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {


        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                xLast = ev.x
                yLast = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (Math.abs(xLast - ev.x) > Math.abs(yLast - ev.y)) {
                    if (xLast > ev.x) {
                        if (forbidToRight) {
                            return false
                        }
                    } else {
                        if (forbidToLeft) {
                            return false
                        }
                    }
                }
            }
        }
        xLast = ev.x
        yLast = ev.y
        return super.onInterceptTouchEvent(ev)
    }

}
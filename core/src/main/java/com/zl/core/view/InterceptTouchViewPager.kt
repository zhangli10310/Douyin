package com.zl.core.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.zl.core.R
import android.view.ViewConfiguration


/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/16 18:05.<br/>
 */
class InterceptTouchViewPager : ViewPager {
    
    private val TAG = InterceptTouchViewPager::class.java.simpleName

    var forbidToLeft: Boolean = false
    var forbidToRight: Boolean = false

    private var mDownX: Float = 0f
    private var mDownY: Float = 0f

    private var mTouchSlop: Int

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.InterceptTouchViewPager)

        forbidToLeft = ta.getBoolean(R.styleable.InterceptTouchViewPager_forbid_to_left, false)
        forbidToRight = ta.getBoolean(R.styleable.InterceptTouchViewPager_forbid_to_right, false)

        ta.recycle()

        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

//    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        return isIntercept?.invoke(ev) ?: true && super.onTouchEvent(ev)
//    }

//    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//
//
//        when (ev.action) {
//            MotionEvent.ACTION_DOWN -> {
//                xLast = ev.x
//                yLast = ev.y
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (Math.abs(xLast - ev.x) > Math.abs(yLast - ev.y)) {
//                    if (xLast > ev.x) {
//                        if (forbidToRight) {
//                            return false
//                        }
//                    } else {
//                        if (forbidToLeft) {
//                            return false
//                        }
//                    }
//                }
//            }
//        }
//        xLast = ev.x
//        yLast = ev.y
//        return super.onInterceptTouchEvent(ev)
//    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var intercept = super.onInterceptTouchEvent(event)
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = x
                mDownY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(x - mDownX)
                val dy = Math.abs(y - mDownY)
                if (!intercept && dx > mTouchSlop && dx * 0.5f > dy) {
                    intercept = true
                }
                if (Math.abs(mDownX - event.x) > Math.abs(mDownY - event.y)) {
                    if (mDownX > event.x) {
                        if (forbidToRight) {
                            intercept = false
                        }
                    } else {
                        if (forbidToLeft) {
                            intercept = false
                        }
                    }
                }
            }
            else -> {
            }
        }
        return intercept
    }

}
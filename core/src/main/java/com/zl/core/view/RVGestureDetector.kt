package com.zl.core.view

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/26 17:39.<br/>
 */
class RVGestureDetector(context: Context, private var listener: RVOnGestureListener) : GestureDetector(context, listener) {

    fun onTouchEvent(ev: MotionEvent, viewHolder: RecyclerView.ViewHolder?): Boolean {
        listener.viewHolder = viewHolder
        return super.onTouchEvent(ev)
    }

    open class RVOnGestureListener : GestureDetector.SimpleOnGestureListener() {
        var viewHolder: RecyclerView.ViewHolder? = null
    }
}
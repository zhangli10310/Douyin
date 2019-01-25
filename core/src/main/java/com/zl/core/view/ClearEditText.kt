package com.zl.core.view

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat

/**
 *
 *<p></p>
 *
 * Created by zhangli.<br/>
 */
class ClearEditText : KeyboardEditText {

    private var mDrawable: Drawable? = null
    private var mDelete = false

    private var mOnContentClearListener: OnContentClearListener? = null

    var onContentClearListener: OnContentClearListener?
        get() {
            return mOnContentClearListener
        }
        set(value) {
            mOnContentClearListener = value
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    // 处理删除事件
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            performClick()
        }
        if (mDelete && null != mDrawable && event.action == MotionEvent.ACTION_UP) {
            val eventX = event.rawX.toInt()
            val eventY = event.rawY.toInt()
            val rect = Rect()
            getGlobalVisibleRect(rect)
            rect.left = rect.right - 50 - paddingRight
            if (rect.contains(eventX, eventY)) {
                setText("")
                mOnContentClearListener?.onContentClear()
            }

        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        setDelete(text != null && text.isNotEmpty() && isFocused && isEnabled)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        setDelete(focused && isEnabled)
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    private fun setDelete(hasDelete: Boolean) {
        if (mDrawable == null) {
            mDrawable = ContextCompat.getDrawable(context, android.R.drawable.presence_offline)
        }
        mDelete = hasDelete
        val drawables = compoundDrawables
        if (hasDelete) {
            if (!TextUtils.isEmpty(text)) {
                setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], mDrawable, drawables[3])
                return
            }
        }
        setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], null, drawables[3])
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setDelete(enabled)
    }

    interface OnContentClearListener {
        fun onContentClear()
    }
}
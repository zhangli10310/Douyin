package com.zl.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/10/24 10:46.<br/>
 */
open class KeyboardEditText : EditText {

    var onKeyBoardHideListener: (() -> Unit)? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == 1) {
            super.onKeyPreIme(keyCode, event)
            onKeyBoardHideListener?.invoke()
            return false
        }
        return super.onKeyPreIme(keyCode, event)
    }
}
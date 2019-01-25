package com.zl.core.extend

import androidx.annotation.LayoutRes
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/30 11:30.<br/>
 */


public fun TextView.toTextString(): String = text.toString().trim()

public fun TextView.isEmpty(): Boolean = TextUtils.isEmpty(toTextString())

public fun TextView.toIntOrNull(): Int? = toTextString().toIntOrNull()

public fun TextView.toLongOrNull(): Long? = toTextString().toLongOrNull()

public fun TextView.toDoubleOrNull(): Double? = toTextString().toDoubleOrNull()

public fun TextView.clear() {
    this.text = ""
}

public fun EditText.addTextChangedListener(listener: (s: CharSequence) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            listener(s)
        }

    })
}

public fun EditText.onEnterDown(listener: (s: CharSequence) -> Unit) {
    this.setOnKeyListener { v, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            listener.invoke(toTextString())
            return@setOnKeyListener true
        } else {
            return@setOnKeyListener false
        }
    }
}

public fun View.setPaddingAll(padding: Int) {
    setPadding(padding, padding, padding, padding)
}

public fun View.inflate(@LayoutRes id: Int, root: ViewGroup? = null, attach: Boolean = false): View {
    return LayoutInflater.from(context).inflate(id, root, attach)
}

public fun RecyclerView.onScrollState(onState: (Int) -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            onState(newState)
        }
    })
}
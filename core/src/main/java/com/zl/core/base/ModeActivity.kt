package com.zl.core.base

import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver


/**
 *
 *<p></p>
 *
 * Created by zhangli<br/>
 */
abstract class ModeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())

        initView(savedInstanceState)
        observe()
        setListener()

        window.decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                afterView()
                window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
    }

    abstract protected fun layoutId(): Int

    abstract protected fun initView(savedInstanceState: Bundle?)

    abstract fun setListener()

    abstract fun observe()

    abstract protected fun afterView()

}
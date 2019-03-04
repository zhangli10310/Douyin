package com.zl.core.base

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
                override fun onGlobalLayout() {
                    afterView()
                    window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        } else {
            afterView()
        }

    }

    protected abstract fun layoutId(): Int

    protected abstract fun initView(savedInstanceState: Bundle?)

    abstract fun setListener()

    abstract fun observe()

    protected abstract fun afterView()

}
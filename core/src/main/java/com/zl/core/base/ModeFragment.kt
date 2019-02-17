package com.zl.core.base

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

/**
 * Created by zhangli<br/>
 */
abstract class ModeFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView(savedInstanceState)
        observe()
        setListener()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                override fun onGlobalLayout() {
                    afterView()
                    view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
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
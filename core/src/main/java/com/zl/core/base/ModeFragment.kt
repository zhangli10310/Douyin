package com.zl.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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
        afterView()
    }

    abstract protected fun layoutId(): Int

    abstract protected fun initView(savedInstanceState: Bundle?)

    abstract fun setListener()

    abstract fun observe()

    abstract protected fun afterView()

}
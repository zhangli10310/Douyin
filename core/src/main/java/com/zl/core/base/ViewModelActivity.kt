package com.zl.core.base

import android.arch.lifecycle.Observer
import android.support.annotation.CallSuper


/**
 *
 *<p></p>
 *
 * Created by zhangli<br/>
 */
abstract class ViewModelActivity<T : BaseViewModel> : ModeActivity() {

    protected lateinit var viewModel: T

    @CallSuper
    override fun observe() {
        initViewModel()

        viewModel.isLoadingInner.observe(this, Observer {
            if (it != null && it) { //正在加载,不显示弹框
                loadingInner()
            } else {
                cancelLoadingInner()
            }
        })

        viewModel.isLoading.observe(this, Observer {
            if (it != null && it) { //正在加载
                showLoading()
            } else {
                hideLoading()
            }
        })

        viewModel.errorMsg.observe(this, Observer {
            if (it != null) {
                showToastSafe("${it.code}-${it.msg}")
            }
        })
    }

    abstract fun initViewModel()

    override fun afterView() {}

    override fun setListener() {}
}
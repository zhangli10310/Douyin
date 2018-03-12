package com.zl.douyin.ui.splash

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.zl.core.base.BaseViewModel

/**
 *
 *<p></p>
 *
 * Created by zhangli.<br/>
 */
class SplashViewModel : BaseViewModel() {


    @Suppress("UNCHECKED_CAST")
    class Factory() : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
                return SplashViewModel() as T
            } else {
                throw RuntimeException("Cannot create an instance of $modelClass, can only create SplashViewModel")
            }
        }
    }
}
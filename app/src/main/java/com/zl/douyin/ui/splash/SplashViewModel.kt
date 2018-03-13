package com.zl.douyin.ui.splash

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.zl.core.base.BaseViewModel
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 *
 *<p></p>
 *
 * Created by zhangli.<br/>
 */
class SplashViewModel : BaseViewModel() {

    var jump: MutableLiveData<Boolean> = MutableLiveData()

    fun countDown() {
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe {
                    jump.postValue(true)
                }
    }

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
package com.zl.core.rxjava

import com.zl.core.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/31 18:58.<br/>
 */
class LoadingTransformer<T>(private var viewModel: BaseViewModel) : ObservableTransformer<T, T> {

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.doOnSubscribe {
            viewModel.isLoading.postValue(true)
        }.doOnTerminate {
            viewModel.isLoading.postValue(false)
        }
    }
}
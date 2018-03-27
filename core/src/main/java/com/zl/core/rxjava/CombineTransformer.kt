package com.zl.core.rxjava

import com.zl.core.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/31 19:13.<br/>
 */
class CombineTransformer<T>(private var viewModel: BaseViewModel) : ObservableTransformer<T, T> {

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(Schedulers.io())
                .compose(LoadingTransformer(viewModel))
    }
}
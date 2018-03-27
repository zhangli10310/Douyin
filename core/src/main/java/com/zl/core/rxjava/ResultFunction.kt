package com.zl.core.rxjava

import com.zl.core.base.BaseResponse
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2017/12/19 14:35.<br/>
 */
class ResultFunction<T> : Function<BaseResponse<T>, ObservableSource<T>> {

    override fun apply(t: BaseResponse<T>): ObservableSource<T> {
        if (t.error_code == 0) {
            return Observable.just(t.data)
        }
        throw NetException(t.message + "", t.error_code)
    }

}
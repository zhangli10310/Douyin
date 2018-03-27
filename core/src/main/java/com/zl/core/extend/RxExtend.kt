package com.zl.core.extend

import com.zl.core.base.BaseResponse
import com.zl.core.base.BaseViewModel
import com.zl.core.base.NetError
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/2/1 10:38.<br/>
 */
public fun <T> Observable<BaseResponse<T>>.douSubscribe(viewModel: BaseViewModel, onNext: (T?) -> Unit, onError: ((Throwable) -> Unit)? = null): Disposable {

    return subscribeOn(Schedulers.io())
            .doOnSubscribe {
                viewModel.isLoading.postValue(true)
            }
            .doOnTerminate {
                viewModel.isLoading.postValue(false)
            }
            .subscribe({
                if (it.error_code == 0) {
                    onNext(it.data)
                } else {
                    viewModel.errorMsg.postValue(NetError(it.error_code, it.message))
                    if (onError != null) {
                        onError(RuntimeException(it.message + ""))
                    }
                }
            }, {
                if (onError == null) {
                    it.printStackTrace()
                    viewModel.errorMsg.postValue(NetError(-1, it.message))
                } else {
                    onError(it)
                }
            })

}

public fun <T> Observable<BaseResponse<T>>.loadingInnerSubscribe(viewModel: BaseViewModel, onNext: (T?) -> Unit, onError: ((Throwable) -> Unit)? = null): Disposable {


    return subscribeOn(Schedulers.io())
            .doOnSubscribe {
                viewModel.isLoadingInner.postValue(true)
            }
            .doOnTerminate {
                viewModel.isLoadingInner.postValue(false)
            }
            .subscribe({
                if (it.error_code == 0) {
                    onNext(it.data)
                } else {
                    viewModel.errorMsg.postValue(NetError(it.error_code, it.message))
                    if (onError != null) {
                        onError(RuntimeException(it.message + ""))
                    }
                }
            }, {
                if (onError == null) {
                    it.printStackTrace()
                    viewModel.errorMsg.postValue(NetError(-1, it.message))
                } else {
                    onError(it)
                }
            })

}

public fun <T> Observable<BaseResponse<T>>.noLoadingSubscribe(viewModel: BaseViewModel, onNext: (T?) -> Unit, onError: ((Throwable) -> Unit)? = null): Disposable {


    return subscribeOn(Schedulers.io())
            .subscribe({
                if (it.error_code == 0) {
                    onNext(it.data)
                } else {
                    viewModel.errorMsg.postValue(NetError(it.error_code, it.message))
                    if (onError != null) {
                        onError(RuntimeException(it.message + ""))
                    }
                }
            }, {
                if (onError == null) {
                    it.printStackTrace()
                    viewModel.errorMsg.postValue(NetError(-1, it.message))
                } else {
                    onError(it)
                }
            })

}
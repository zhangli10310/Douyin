package com.zl.core.extend

import com.zl.core.api.data.BaseResult
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
public fun <T : BaseResult> Observable<T>.douSubscribe(viewModel: BaseViewModel, onNext: (T) -> Unit, onError: ((Throwable) -> Unit)? = null): Disposable {

    return subscribeOn(Schedulers.io())
            .doOnSubscribe {
                viewModel.isLoading.postValue(true)
            }
            .doOnTerminate {
                viewModel.isLoading.postValue(false)
            }
            .subscribe({
                if (it.status_code == 0) {
                    onNext(it)
                } else {
                    viewModel.errorMsg.postValue(NetError(it.status_code, "fixme: RxExtend.kt line-29"))
                    if (onError != null) {
                        onError(RuntimeException("fixme: RxExtend.kt line-31"))
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

public fun <T : BaseResult> Observable<T>.loadingInnerSubscribe(viewModel: BaseViewModel, onNext: (T) -> Unit, onError: ((Throwable) -> Unit)? = null): Disposable {


    return subscribeOn(Schedulers.io())
            .doOnSubscribe {
                viewModel.isLoadingInner.postValue(true)
            }
            .doOnTerminate {
                viewModel.isLoadingInner.postValue(false)
            }
            .subscribe({
                if (it.status_code == 0) {
                    onNext(it)
                } else {
                    viewModel.errorMsg.postValue(NetError(it.status_code, "fixme: RxExtend.kt line-59"))
                    if (onError != null) {
                        onError(RuntimeException("fixme: RxExtend.kt line-61"))
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

public fun <T: BaseResult> Observable<T>.noLoadingSubscribe(viewModel: BaseViewModel, onNext: (T) -> Unit, onError: ((Throwable) -> Unit)? = null): Disposable {


    return subscribeOn(Schedulers.io())
            .subscribe({
                if (it.status_code == 0) {
                    onNext(it)
                } else {
                    viewModel.errorMsg.postValue(NetError(it.status_code, "fixme: RxExtend.kt line-83"))
                    if (onError != null) {
                        onError(RuntimeException("fixme: RxExtend.kt line-85"))
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
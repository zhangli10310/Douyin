package com.zl.core.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *
 *<p></p>
 *
 * Created by zhangli<br/>
 */
open class BaseViewModel : ViewModel() {

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()  //是否正在加载
    var isLoadingInner: MutableLiveData<Boolean> = MutableLiveData()  //是否正在加载,不显示弹框
    var errorMsg: MutableLiveData<NetError> = MutableLiveData()

}
package com.zl.douyin.update

import com.zl.core.BuildConfig
import com.zl.core.api.ServiceGenerator
import com.zl.core.base.BaseResponse
import io.reactivex.Observable

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/2/27 11:06.<br/>
 */
class UpdateRepository {

    private var mService: UpdateApi = ServiceGenerator.createRxService(UpdateApi::class.java, BuildConfig.BASE_URL)

    companion object {
        fun get(): UpdateRepository {
            return Instance.repository
        }
    }

    private object Instance {
        val repository = UpdateRepository()
    }

    fun checkUpdate(): Observable<BaseResponse<UpdateResult?>> {
        val param = CheckUpdateParam()
        return mService.checkUpdate(param)
    }
}
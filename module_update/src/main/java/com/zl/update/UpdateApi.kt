package com.zl.update

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/31 11:34.<br/>
 * Copyright (c) 2015年 Beijing Yunshan Information Technology Co., Ltd. All rights reserved.<br/>
 */
interface UpdateApi {

    @Headers("Env-Domain: wmc-gateway")
    @POST("api/wmcMasterConf/clientVersionService/getUpdataUrl")
    fun checkUpdate(@Body param: CheckUpdateParam): Observable<UpdateResult>

}
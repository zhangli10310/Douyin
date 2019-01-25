package com.zl.update

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/31 11:35.<br/>
 */
interface DownloadApi {

    @Streaming
    @GET
    fun downloadFileWithFixedUrl(@Url url: String): Observable<ResponseBody>
}
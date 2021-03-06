package com.zl.douyin.ui.comment

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/26 10:34.<br/>
 */
interface CommentService {

    @GET("aweme/v1/comment/list/")
    fun loadComment(@QueryMap map: Map<String, String>): Observable<CommentData>
}
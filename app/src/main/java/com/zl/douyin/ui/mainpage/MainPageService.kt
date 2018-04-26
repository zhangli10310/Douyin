package com.zl.douyin.ui.mainpage

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
interface MainPageService {

    @GET
    fun loadRecommendVideo(@Url url: String, @QueryMap map: Map<String, String>): Observable<FeedData>
}
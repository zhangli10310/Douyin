package com.zl.douyin.ui.mainpage

import com.zl.core.api.data.BaseResult
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

    //推荐视频
    @GET("aweme/v1/feed/")
    fun loadRecommendVideo(@QueryMap map: Map<String, String>): Observable<FeedData>

    //播放统计
    @GET("aweme/v1/aweme/stats/")
    fun stats(@QueryMap map: Map<String, String>): Observable<BaseResult>
}
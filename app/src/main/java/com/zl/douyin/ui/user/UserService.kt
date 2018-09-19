package com.zl.douyin.ui.user

import com.zl.douyin.ui.mainpage.FeedData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface UserService {

    @GET("aweme/v1/user/")
    fun queryUser(@QueryMap map: Map<String, String>): Observable<UserData>

    @GET("aweme/v1/aweme/post/")
    fun queryAwe(@QueryMap map: Map<String, String>): Observable<FeedData>

    @GET("aweme/v1/forward/list/")
    fun queryDongtai(@QueryMap map: Map<String, String>): Observable<FeedData>

    @GET("aweme/v1/aweme/favorite/")
    fun queryFavorite(@QueryMap map: Map<String, String>): Observable<FeedData>
}
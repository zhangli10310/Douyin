package com.zl.douyin.ui.user

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface UserService {

    @GET("aweme/v1/user/")
    fun queryUser(@QueryMap map: Map<String, String>): Observable<UserData>
}
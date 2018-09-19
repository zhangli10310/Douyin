package com.zl.douyin.ui.user

import com.zl.core.BuildConfig
import com.zl.core.api.ServiceGenerator
import io.reactivex.Observable
import com.google.gson.Gson
import com.zl.core.MainApp
import com.zl.douyin.ui.mainpage.FeedData
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/26 10:32.<br/>
 */
class UserRepository public constructor() {

    private val TAG = UserRepository::class.java.simpleName

    private var mService: UserService = ServiceGenerator.createRxService(UserService::class.java, BuildConfig.BASE_URL)


    companion object {
        fun get(): UserRepository {
            return Instance.repository
        }
    }

    private object Instance {
        val repository = UserRepository()
    }

    fun queryUser(userId: String): Observable<UserData> {
        val map = mapOf(
                Pair("user_id", userId)
        )
        return mService.queryUser(map)
    }

    fun queryAwe(userId: String, maxCursor: String): Observable<FeedData> {
        val map = mapOf(
                Pair("user_id", userId),
                Pair("max_cursor", maxCursor),
                Pair("count", "20")
        )
        return mService.queryAwe(map)
    }

    fun queryDongtai(userId: String, maxCursor: String): Observable<FeedData> {
        val map = mapOf(
                Pair("user_id", userId),
                Pair("max_cursor", maxCursor),
                Pair("count", "20")
        )
        return mService.queryDongtai(map)
    }

    fun queryFavorite(userId: String, maxCursor: String): Observable<FeedData> {
        val map = mapOf(
                Pair("user_id", userId),
                Pair("max_cursor", maxCursor),
                Pair("count", "20")
        )
        return mService.queryFavorite(map)
    }
}
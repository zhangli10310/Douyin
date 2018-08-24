package com.zl.douyin.ui.comment

import com.zl.core.BuildConfig
import com.zl.core.api.ServiceGenerator
import io.reactivex.Observable
import com.google.gson.Gson
import com.zl.core.MainApp
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/26 10:32.<br/>
 */
class CommentRepository public constructor() {

    private val TAG = CommentRepository::class.java.simpleName

    private var mService: CommentService = ServiceGenerator.createRxService(CommentService::class.java, BuildConfig.API_URL)


    companion object {
        fun get(): CommentRepository {
            return Instance.repository
        }
    }

    private object Instance {
        val repository = CommentRepository()
    }

    fun loadComment(awemeId: Long?, cursor: Int): Observable<CommentData> {
        val map = mapOf(
                Pair("aweme_id", awemeId.toString()),
                Pair("cursor", cursor.toString()),
                Pair("count", "20")
        )
        return mService.loadComment(map)
    }

}
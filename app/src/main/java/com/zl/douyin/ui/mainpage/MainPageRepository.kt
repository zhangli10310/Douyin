package com.zl.douyin.ui.mainpage

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
class MainPageRepository public constructor() {

    private val TAG = MainPageRepository::class.java.simpleName

    private var mService: MainPageService = ServiceGenerator.createRxService(MainPageService::class.java, BuildConfig.BASE_URL)


    companion object {
        fun get(): MainPageRepository {
            return Instance.repository
        }
    }

    private object Instance {
        val repository = MainPageRepository()
    }

    fun loadRecommendVideo(): Observable<FeedData> {
        //fixme
//        return Observable.create({
//            try {
//                val stringBuilder = StringBuilder()
//                //获取assets资源管理器
//                val assetManager = MainApp.instance.getAssets()
//                //通过管理器打开文件并读取
//                val bf = BufferedReader(InputStreamReader(
//                        assetManager.open("feed.json")))
//                var line = bf.readLine()
//                while (line != null) {
//                    stringBuilder.append(line)
//                    line = bf.readLine()
//                }
//                val gson = Gson()
//                val data = gson.fromJson<FeedData>(stringBuilder.toString(), FeedData::class.java)
//                it.onNext(data)
//                it.onComplete()
//            } catch (e: Exception) {
//                it.onError(e)
//            }
//
//
//        })
        val map = mapOf(
                Pair("type", "0"),
                Pair("max_cursor", "0"),
                Pair("min_cursor", "0"),
                Pair("count", "6"),
                Pair("volume", "0.13333333333333333"),
                Pair("pull_type", "1"),
                Pair("need_relieve_aweme", "0")
        )
        return mService.loadRecommendVideo(url = "${BuildConfig.API_URL}aweme/v1/feed/", map = map)
    }
}
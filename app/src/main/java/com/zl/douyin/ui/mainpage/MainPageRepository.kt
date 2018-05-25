package com.zl.douyin.ui.mainpage

import com.zl.core.BuildConfig
import com.zl.core.api.ServiceGenerator
import io.reactivex.Observable

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/26 10:32.<br/>
 */
class MainPageRepository private constructor() {

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
        val list = mutableListOf<VideoEntity>()
        list.add(VideoEntity("rtmp://47.52.146.113:1935/rtmplive/good"))
        list.add(VideoEntity("https://aweme.snssdk.com/aweme/v1/play/?video_id=cb04291f17fd423daa6a81f42fe41d76&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        list.add(VideoEntity("https://aweme.snssdk.com/aweme/v1/play/?video_id=92a76d5253cd40469ecaf765302bc335&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        list.add(VideoEntity("https://aweme.snssdk.com/aweme/v1/play/?video_id=f660eced34864bd6babe7448f46a5356&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        list.add(VideoEntity("https://aweme.snssdk.com/aweme/v1/play/?video_id=2f16b886877c44a589fc0d1f169581fd&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        val data = FeedData()
        data.status_code = 0
        data.aweme_list.addAll(list)
        return Observable.just(data)
                .map {
                    Thread.sleep(2000)
                    return@map it
                }
//        val map = mapOf(
//                Pair("type", "0"),
//                Pair("max_cursor", "0"),
//                Pair("min_cursor", "0"),
//                Pair("count", "6"),
//                Pair("volume", "0.06666666666666667"),
//                Pair("pull_type", "1"),
//                Pair("need_relieve_aweme", "0")
//        )
//        return mService.loadRecommendVideo(url = "${BuildConfig.API_URL}aweme/v1/feed/", map = map)
    }
}
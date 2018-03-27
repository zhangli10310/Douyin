package com.zl.douyin.ui.mainpage

import android.util.Log
import com.zl.core.BuildConfig
import com.zl.core.api.ServiceGenerator
import com.zl.core.base.BaseResponse
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

    fun loadRecommendVideo(): Observable<BaseResponse<MutableList<VideoEntity>>> {
        //fixme
        val list = mutableListOf<VideoEntity>()
        list.add(VideoEntity("https://aweme.snssdk.com/aweme/v1/play/?video_id=cb04291f17fd423daa6a81f42fe41d76&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        list.add(VideoEntity("https://api.amemv.com/aweme/v1/play/?video_id=cb04291f17fd423daa6a81f42fe41d76&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        list.add(VideoEntity("https://aweme.snssdk.com/aweme/v1/play/?video_id=cb04291f17fd423daa6a81f42fe41d76&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        list.add(VideoEntity("https://api.amemv.com/aweme/v1/play/?video_id=cb04291f17fd423daa6a81f42fe41d76&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        return Observable.just(BaseResponse(list, 0, null))
                .map {
                    Thread.sleep(2000)
                    return@map it
                }
    }
}
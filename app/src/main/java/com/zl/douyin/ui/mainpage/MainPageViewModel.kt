package com.zl.douyin.ui.mainpage

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.zl.core.base.BaseViewModel
import com.zl.core.extend.loadingInnerSubscribe
import com.zl.core.extend.noLoadingSubscribe

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/26 10:31.<br/>
 */
class MainPageViewModel(private var repository: MainPageRepository) : BaseViewModel() {

    private val TAG = MainPageViewModel::class.java.simpleName

    var videoList: MutableLiveData<MutableList<FeedItem>> = MutableLiveData()
    var moreVideoList: MutableLiveData<MutableList<FeedItem>> = MutableLiveData()
    var loadingRecommendStatus: MutableLiveData<Int> = MutableLiveData()

    fun loadRecommendVideo() {

        repository.loadRecommendVideo()
                .doOnTerminate {
                    loadingRecommendStatus.postValue(1)
                }
                .loadingInnerSubscribe(this, {
                    videoList.postValue(it.aweme_list)
                })
    }

    fun loadMoreVideo() {
        //fixme
        repository.loadRecommendVideo()
                .noLoadingSubscribe(this, {
                    moreVideoList.postValue(it.aweme_list)
                })
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private var repository: MainPageRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(MainPageViewModel::class.java)) {
                return MainPageViewModel(repository) as T
            } else {
                throw RuntimeException("Cannot create an instance of $modelClass, can only create PickGoodMainViewModel")
            }
        }
    }
}
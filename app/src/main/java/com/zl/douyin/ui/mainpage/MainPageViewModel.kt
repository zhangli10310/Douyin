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

    private var hasMore: Boolean = true
    private var loading: Boolean = false

    fun loadRecommendVideo() {

        repository.loadRecommendVideo()
                .doOnTerminate {
                    loadingRecommendStatus.postValue(1)
                }
                .loadingInnerSubscribe(this, onNext = {
                    videoList.postValue(it.aweme_list)
                })
    }

    fun loadMoreVideo() {

        if (hasMore && !loading) {
            loading = true
            repository.loadMoreVideo()
                    .doOnTerminate {
                        loading = false
                    }
                    .noLoadingSubscribe(this, {
                        hasMore = it.has_more == 1
                        moreVideoList.postValue(it.aweme_list)
                    }, {
                        hasMore = false
                    })
        }

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
package com.zl.douyin.ui.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zl.core.base.BaseViewModel
import com.zl.core.extend.noLoadingSubscribe
import com.zl.douyin.ui.mainpage.FeedItem


class UserViewModel(var repository: UserRepository) : BaseViewModel() {

    var userInfo = MutableLiveData<UserEntity>()

    var hasMoreAwe = MutableLiveData<Boolean>()
    var maxAweCursor = MutableLiveData<String>()
    var moreAweVideoList = MutableLiveData<MutableList<FeedItem>?>()

    var hasMoreFavorite = MutableLiveData<Boolean>()
    var maxFavoriteCursor = MutableLiveData<String>()
    var moreFavoriteVideoList = MutableLiveData<MutableList<FeedItem>?>()

    var loadingAwe = MutableLiveData<Boolean>()
    var loadingFavorite = MutableLiveData<Boolean>()

    fun queryUser(uid: String) {
        repository.queryUser(uid)
                .noLoadingSubscribe(this, onNext = {
                    userInfo.postValue(it.user)
                })
    }

    fun queryAwe(uid: String, maxCursor: String) {
        repository.queryAwe(uid, maxCursor)
                .doOnSubscribe {
                    loadingAwe.postValue(true)
                }
                .doOnTerminate {
                    loadingAwe.postValue(false)
                }
                .noLoadingSubscribe(this, {
                    maxAweCursor.postValue(it.max_cursor.toString())
                    if (it.has_more != 1) {
                        hasMoreAwe.postValue(false)
                    } else {
                        hasMoreAwe.postValue(true)
                    }
                    moreAweVideoList.postValue(it.aweme_list)
                }, {
                    hasMoreAwe.postValue(false)
                })
    }

    fun queryFavorite(uid: String, maxCursor: String) {
        repository.queryFavorite(uid, maxCursor)
                .doOnSubscribe {
                    loadingFavorite.postValue(true)
                }
                .doOnTerminate {
                    loadingFavorite.postValue(false)
                }
                .noLoadingSubscribe(this, {
                    maxFavoriteCursor.postValue(it.max_cursor.toString())
                    if (it.has_more != 1) {
                        hasMoreFavorite.postValue(false)
                    } else {
                        hasMoreFavorite.postValue(true)
                    }
                    moreFavoriteVideoList.postValue(it.aweme_list)
                }, {
                    hasMoreFavorite.postValue(false)
                })
    }


    @Suppress("UNCHECKED_CAST")
    class Factory(private var repository: UserRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                return UserViewModel(repository) as T
            } else {
                throw RuntimeException("Cannot create an instance of $modelClass, can only create UserViewModel")
            }
        }
    }
}
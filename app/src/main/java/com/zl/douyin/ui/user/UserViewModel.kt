package com.zl.douyin.ui.user

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.zl.core.base.BaseViewModel
import com.zl.core.extend.noLoadingSubscribe


class UserViewModel(var repository: UserRepository) : BaseViewModel() {

    var userInfo = MutableLiveData<UserEntity>()

    fun queryUser(uid: String) {
        repository.queryUser(uid)
                .noLoadingSubscribe(this, {
                    userInfo.postValue(it.user)
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
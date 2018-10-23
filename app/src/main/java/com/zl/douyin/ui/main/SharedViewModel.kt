package com.zl.douyin.ui.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.zl.douyin.ui.user.UserEntity


class SharedViewModel : ViewModel() {

    var currentSelectUser = MutableLiveData<UserEntity>()
    var queryUser = MutableLiveData<Boolean>()

    var gotoViewPagerPosition = MutableLiveData<Int>() //命令Viewpager跳转
    var onViewPagerChange = MutableLiveData<Int>() //viewPager跳转回调

    fun changeViewPagerPosition(pos: Int) {
        gotoViewPagerPosition.postValue(pos)
    }

    fun onViewPagerPositionChange(pos: Int) {
        onViewPagerChange.postValue(pos)
    }

}
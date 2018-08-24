package com.zl.douyin.ui.comment

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
class CommentViewModel(private var repository: CommentRepository) : BaseViewModel() {

    private val TAG = CommentViewModel::class.java.simpleName

    var moreCommentList: MutableLiveData<CommentData> = MutableLiveData()

    var hasMore: MutableLiveData<Boolean> = MutableLiveData()

    fun loadComment(awemeId: Long?, cursor: Int) {
        repository.loadComment(awemeId, cursor)
                .noLoadingSubscribe(this, {
                    if (it.has_more != 1) {
                        hasMore.postValue(false)
                    } else {
                        hasMore.postValue(true)
                    }
                    moreCommentList.postValue(it)
                }, {
                    hasMore.postValue(false)
                })
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private var repository: CommentRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
                return CommentViewModel(repository) as T
            } else {
                throw RuntimeException("Cannot create an instance of $modelClass, can only create CommentViewModel")
            }
        }
    }
}
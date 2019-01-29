package com.zl.douyin.ui.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zl.core.base.BaseViewModel
import com.zl.core.extend.noLoadingSubscribe

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/26 10:31.<br/>
 */
class CommentViewModel(private var repository: CommentRepository) : BaseViewModel() {

    private val TAG = CommentViewModel::class.java.simpleName

    var allCommentList:MutableLiveData<MutableList<CommentItem>> = MutableLiveData()
    var lastComment: MutableLiveData<CommentData?> = MutableLiveData()

    private var loading = false
    private var hasMore = true

    var awemeId = 0L

    fun loadComment() {
        var cursor = 0L

        lastComment.value?.let {
            cursor = it.cursor ?: 0L
        }

        if (hasMore && !loading) {
            loading = true
            repository.loadComment(awemeId, cursor)
                    .doOnTerminate {
                        loading = false
                    }
                    .noLoadingSubscribe(this, {
                        hasMore = it.has_more == 1

                        it.comments?.let {
                            val list = allCommentList.value!!
                            list.addAll(it)
                            allCommentList.postValue(list)
                        }

                        lastComment.postValue(it)
                    }, {
                        hasMore = false
                    })
        }
    }

    fun reset() {
        lastComment.value = null
        allCommentList.value = mutableListOf()
        loading = false
        hasMore = true
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
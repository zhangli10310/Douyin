package com.zl.douyin.ui.comment

import com.zl.core.api.data.BaseResult
import com.zl.douyin.ui.user.UserEntity


class CommentData : BaseResult() {

    var has_more = -1
    var total:Int? = null
    var comments: MutableList<CommentItem>? = null
}

class CommentItem {
    var text: String? = null
    var create_time: Int? = null
    var user_digged: Int? = null
    var digg_count: Int? = null
    var user: UserEntity? = null
}
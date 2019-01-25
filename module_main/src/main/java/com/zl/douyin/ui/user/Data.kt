package com.zl.douyin.ui.user

import android.view.View
import com.zl.core.api.data.BaseResult
import com.zl.douyin.ui.mainpage.PlayEntity

class UserData : BaseResult() {

    var user: UserEntity? = null
}

class UserEntity {

    var uid: String? = null
    var nickname: String? = null
    var gender: Int? = null
    var avatar_thumb: PlayEntity? = null

    var short_id: String? = null
    var unique_id: String? = null
    var signature: String? = null
    var birthday: String? = null

    var location: String? = null
    var dongtai_count: Long? = null
    var favoriting_count: Long? = null
    var aweme_count: Long? = null

    var total_favorited: Long? = null
    var following_count: Long? = null
    var follower_count: Long? = null
}

data class TitleView(var title: String, var view: View)
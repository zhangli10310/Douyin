package com.zl.douyin.ui.mainpage

import com.zl.core.api.data.BaseResult
import com.zl.douyin.ui.user.UserEntity

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/4/26 15:45.<br/>
 */
class FeedData : BaseResult() {

    var aweme_list: MutableList<FeedItem> = mutableListOf()
    var has_more: Int = -1
}

class FeedItem {

    var desc: String? = null
    var video: VideoItem? = null
    var music: MusicEntity? = null
    var author: UserEntity? = null
    var statistics: StatisticsEntity? = null
    var user_digged: Int? = null
    var aweme_id: Long? = null
}

class MusicEntity {
    var title: String? = null
    var owner_nickname: String? = null
    var cover_thumb: PlayEntity? = null
}

class VideoItem {
    var origin_cover: PlayEntity? = null
    var play_addr: PlayEntity? = null
}

class StatisticsEntity {
    var comment_count: Long? = null
    var share_count: Long? = null
    var digg_count: Long? = null
}

class PlayEntity {
    var url_list: List<String>? = null
    var uri: String? = null
}
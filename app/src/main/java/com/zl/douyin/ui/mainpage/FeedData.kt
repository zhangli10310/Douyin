package com.zl.douyin.ui.mainpage

import com.zl.core.api.data.BaseResult

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/4/26 15:45.<br/>
 */
class FeedData : BaseResult() {

    var aweme_list: MutableList<VideoEntity> = mutableListOf()
}

class VideoEntity {

    constructor(url: String?, headUrl: String? = null) {
        this.url = url
        this.headUrl = headUrl
    }

    var url: String? = null
    var headUrl: String? = null
}
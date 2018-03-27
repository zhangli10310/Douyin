package com.zl.douyin.ui.mainpage

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/23 16:35.<br/>
 */
class VideoEntity {

    constructor(url: String?, headUrl: String? = null) {
        this.url = url
        this.headUrl = headUrl
    }

    var url: String? = null
    var headUrl: String? = null
}
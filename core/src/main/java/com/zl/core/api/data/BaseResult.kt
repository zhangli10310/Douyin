package com.zl.core.api.data

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/4/26 15:32.<br/>
 */
open class BaseResult {
    var status_code: Int = -1
    var extra: ExtraResult? = null
    var status_msg: String? = null
    var max_cursor: Long? = null
}
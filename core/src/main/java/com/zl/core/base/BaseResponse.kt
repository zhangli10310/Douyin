package com.zl.core.base

/**
 *
 *<p></p>
 *
 * Created by zhangli<br/>
 * Copyright (c) 2015年 Beijing Yunshan Information Technology Co., Ltd. All rights reserved.<br/>
 */
data class BaseResponse<out T>(
        val data: T?,
        val error_code: Int = -1,
        val message: String?
)
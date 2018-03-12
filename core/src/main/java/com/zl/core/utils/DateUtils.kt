package com.zl.core.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/30 16:08.<br/>
 */
object DateUtils {

    val DEFAULT_FORMAT = "yyyy-MM-dd"

    fun timeMillisToDateString(timeMillis: Long, format: String = DEFAULT_FORMAT): String {
        val f = SimpleDateFormat(format, Locale.CHINA)
        return f.format(timeMillis)
    }
}
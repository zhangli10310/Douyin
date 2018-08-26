package com.zl.core.utils

/**
 * Created by zhangli on 2018/8/26,09:26<br/>
 */
object CommonUtils {

    fun formatCount(number: Long?): String {
        return when (number) {
            null -> {
                "0"
            }
            in 0..9999 -> {
                number.toString()
            }
            else -> {
                (number / 10000).toString() + "." + (number / 1000 % 10).toString() + "w"
            }
        }
    }

}
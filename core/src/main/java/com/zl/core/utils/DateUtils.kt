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
        val f = SimpleDateFormat(format, Locale.getDefault())
        return f.format(timeMillis)
    }

    fun dateStringToTimeMillis(date: String, format: String = DEFAULT_FORMAT): Long {
        val f = SimpleDateFormat(format, Locale.getDefault())
        return try {
            f.parse(date).time
        } catch (e: Exception) {
            0
        }
    }

    private val dayArr = intArrayOf(20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22)
    private val constellationArr = arrayOf("摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座")


    fun getConstellation(month: Int, day: Int): String {
        return if (day < dayArr[month - 1]) constellationArr[month - 1] else constellationArr[month]
    }

    fun getConstellation(timeMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        return getConstellation(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
    }
}
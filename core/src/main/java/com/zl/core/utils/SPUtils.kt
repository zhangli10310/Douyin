package com.zl.core.utils

import android.content.Context
import com.zl.core.MainApp

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/4/26 17:53.<br/>
 */
object SPUtils {

    val sp = MainApp.instance.getSharedPreferences("douyin", Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        sp.edit().putString(key, value).apply()
    }

    fun getString(key: String, defValue: String? = null): String? {
        return sp.getString(key, defValue)
    }
}
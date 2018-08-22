package com.zl.core.extend


/**
 * 根据[toString]转为string数组，通常用于[android.app.AlertDialog]的setItem
 *
 */
fun <T> List<T>.toStringArray(default: String = ""): Array<String> {
    val array = Array(size, { default })
    for (i in 0 until size) {
        array[i] = this[i].toString()
    }
    return array
}
package com.zl.core.extend

import com.zl.core.utils.ReflectUtils


fun Any.reflectSetField(fieldName: String, value: Any?) {
    ReflectUtils.setFieldValue(this, fieldName, value)
}
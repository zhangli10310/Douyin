package com.zl.core.rxjava

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2017/12/19 17:32.<br/>
 */
class NetException(var msg: String, var code:Int): RuntimeException(msg)
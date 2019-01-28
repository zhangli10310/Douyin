package com.zl.core

import android.content.Context
import android.util.Log
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.DegradeService

/**
 *
 * Created by zhangli on 2019/1/28 14:42.
 */
@Route(path = "/degrade/degrade")
class DegradeServiceImpl: DegradeService {

    private val TAG = DegradeServiceImpl::class.java.simpleName

    override fun onLost(context: Context?, postcard: Postcard?) {
        Log.i(TAG, "onLost: $context, $postcard")
    }

    override fun init(context: Context?) {

    }
}
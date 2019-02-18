package com.zl.live.ui

import android.net.Uri
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.zl.core.Router
import com.zl.core.base.ModeActivity
import com.zl.live.R
import kotlinx.android.synthetic.main.activity_live_room.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/12/26 15:23.<br/>
 */
@Route(path = Router.LIVE_ACTIVITY)
class LiveRoomActivity : ModeActivity(){

    override fun layoutId() = R.layout.activity_live_room

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun setListener() {
    }

    override fun observe() {
    }

    override fun afterView() {

        videoView.setVideoURI(Uri.parse(""))
        videoView.start()
    }
}
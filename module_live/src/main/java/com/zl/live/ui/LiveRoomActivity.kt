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

        videoView.setVideoURI(Uri.parse("https://api.amemv.com/aweme/v1/play/?video_id=v0200f530000bdt78p3pqv6acajvuebg&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        videoView.start()
    }
}
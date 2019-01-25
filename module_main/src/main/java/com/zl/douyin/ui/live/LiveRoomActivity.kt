package com.zl.douyin.ui.live

import android.net.Uri
import android.os.Bundle
import com.zl.core.base.ModeActivity
import com.zl.douyin.R
import kotlinx.android.synthetic.main.activity_live_room.*
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/12/26 15:23.<br/>
 */
class LiveRoomActivity : ModeActivity(){

    override fun layoutId() = R.layout.activity_live_room

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun setListener() {
    }

    override fun observe() {
    }

    override fun afterView() {

        videoView.setOnErrorListener { _, _, _ ->
            videoView.start()
            true
        }

        videoView.setVideoURI(Uri.parse("rtmp://47.91.156.168:1935/live/1"))
        videoView.start()
    }
}
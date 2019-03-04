package com.zl.douyin.ui.littlefilm

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.zl.core.base.ModeFragment
import com.zl.douyin.R
import com.zl.douyin.ui.main.SharedViewModel
import kotlinx.android.synthetic.main.fragment_little_film.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2019/3/4 15:26.<br/>
 */
class LittleFilmFragment : ModeFragment() {

    private lateinit var shareViewModel: SharedViewModel

    override fun layoutId() = R.layout.fragment_little_film

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun setListener() {

        closeImg.setOnClickListener {
            shareViewModel.gotoViewPagerPosition.value = 1
        }
    }

    override fun observe() {
        shareViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
    }

    override fun afterView() {

    }
}
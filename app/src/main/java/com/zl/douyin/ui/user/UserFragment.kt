package com.zl.douyin.ui.user

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.zl.core.base.ModeFragment
import com.zl.core.utils.DisplayUtils
import com.zl.core.view.GridSpacingItemDecoration
import com.zl.douyin.R
import com.bumptech.glide.Glide
import com.zl.core.utils.CommonUtils
import com.zl.core.utils.DateUtils
import com.zl.core.utils.GlideUtils
import com.zl.core.view.AppBarStateChangeListener
import com.zl.douyin.ui.main.SharedViewModel
import com.zl.douyin.ui.mainpage.FeedItem
import kotlinx.android.synthetic.main.fragment_user.*


/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/16 18:02.<br/>
 */
class UserFragment : ModeFragment() {

    private val TAG = UserFragment::class.java.simpleName

    private var userEntity: UserEntity? = null

    private lateinit var shareViewModel: SharedViewModel
    private lateinit var userViewModel: UserViewModel

    private var list: MutableList<TitleView> = mutableListOf()
    private lateinit var mUserVideoAdapter: UserVideoAdapter


    override fun initView(savedInstanceState: Bundle?) {

//        recyclerView.layoutManager = GridLayoutManager(activity, 3)
//        recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount = 3, space = DisplayUtils.dp2px(activity!!, 1f).toInt(), includeEdge = false))
//        recyclerView.adapter = UserVideoAdapter()
//        recyclerView.setHasFixedSize(true)
//        recyclerView.isNestedScrollingEnabled = false

        list.clear()
        list.add(TitleView("作品0", getAweView()))
        list.add(TitleView("动态0", getDongtaiView()))
        list.add(TitleView("喜欢0", getFavoritingView()))
        mUserVideoAdapter = UserVideoAdapter(list)
        viewPager.adapter = mUserVideoAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private var hasMoreAwe: Boolean = true
    private var maxAweCursor: String = "0"
    private var aweList: MutableList<FeedItem> = mutableListOf()
    private lateinit var aweAdapter: VideoGridAdapter

    private fun getAweView(): View {

        aweAdapter = VideoGridAdapter(aweList)

        return RecyclerView(activity!!).apply {
            layoutManager = GridLayoutManager(activity, 3)
            addItemDecoration(GridSpacingItemDecoration(spanCount = 3, space = DisplayUtils.dp2px(activity!!, 1f).toInt(), includeEdge = false, color = Color.BLACK))
            adapter = aweAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> { //当屏幕停止滚动

                            val layoutManager = recyclerView.layoutManager as GridLayoutManager
                            val last = layoutManager.findLastVisibleItemPosition()

                            if ((last + layoutManager.spanCount + 3) > list.size && hasMoreAwe && userViewModel.isLoading.value != true) {
                                userEntity?.uid?.let {
                                    userViewModel.queryAwe(it, maxAweCursor)
                                }
                            }
                        }

                    }
                }
            })
        }
    }

    private fun getDongtaiView(): View {
        return RecyclerView(activity!!)
    }

    private fun getFavoritingView(): View {
        return RecyclerView(activity!!)
    }

    override fun setListener() {
        backImg.setOnClickListener {
            activity!!.onBackPressed()
        }

        appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: Int) {
                when (state) {
                    AppBarStateChangeListener.EXPANDED -> {
                        Log.i(TAG, "onStateChanged: 展开状态")
                        //展开状态
                        titleText.visibility = View.GONE
                    }
                    AppBarStateChangeListener.COLLAPSED -> {
                        Log.i(TAG, "onStateChanged: 折叠状态")
                        //折叠状态
                        titleText.visibility = View.VISIBLE
                    }
                    else -> {
                        Log.i(TAG, "onStateChanged: 中间状态")
                        //中间状态
                        titleText.visibility = View.GONE
                    }
                }
            }
        })
    }

    override fun observe() {
        userViewModel = ViewModelProviders.of(activity!!, UserViewModel.Factory(UserRepository.get())).get(UserViewModel::class.java)
        shareViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        shareViewModel.currentSelectUser.observe(this, Observer {
            resetInfo()
            userViewModel.userInfo.postValue(it)
        })

        shareViewModel.queryUser.observe(this, Observer {
            if (it != null && it) {
                userEntity?.uid?.let {
                    userViewModel.queryUser(it)
                    userViewModel.queryAwe(it, maxAweCursor)
                }
            }
        })

        userViewModel.hasMoreAwe.observe(this, Observer {
            hasMoreAwe = it ?: true
        })

        userViewModel.maxAweCursor.observe(this, Observer {
            maxAweCursor = it ?: "0"
        })

        userViewModel.moreAweVideoList.observe(this, Observer {
            if (it != null) {
                aweList.addAll(it)
                aweAdapter.notifyDataSetChanged()
            }
        })

        userViewModel.userInfo.observe(this, Observer {
            if (it != null) {
                userEntity = it
                it.avatar_thumb?.url_list?.let {
                    GlideUtils.load(it, headImg)
                    GlideUtils.load(it, headBlurImg)
                }
                douyinCodeText.text = "抖音号:" + it.short_id
                if (it.nickname.isNullOrBlank()) {
                    nameText.text = "昵称加载中"
                } else {
                    nameText.text = it.nickname
                }
                titleText.text = it.nickname
                if (it.signature.isNullOrBlank()) {
                    describeText.text = "本宝宝暂时还没想到个性的签名"
                } else {
                    describeText.text = it.signature
                }

                var gender = ""
                val birthday = DateUtils.dateStringToTimeMillis(it.birthday + "")
                if (it.gender == 1) {
                    gender = "♂"
                } else if (it.gender == 2) {
                    gender = "♀"
                }
                if (birthday > 0) {
                    ageText.text = "$gender ${(System.currentTimeMillis() - birthday) / 1000 / 60 / 60 / 24 / 365}岁"
                    starText.text = DateUtils.getConstellation(birthday)
                }

                cityText.text = it.location

                it.total_favorited.let {
                    likeText.text = CommonUtils.formatCount(it) + "获赞"
                }

                it.following_count.let {
                    focusText.text = CommonUtils.formatCount(it) + "关注"
                }

                it.follower_count.let {
                    fansText.text = CommonUtils.formatCount(it) + "粉丝"
                }

                it.aweme_count.let {
                    list[0].title = "作品" + CommonUtils.formatCount(it)
                }

                it.dongtai_count.let {
                    list[1].title = "动态" + CommonUtils.formatCount(it)
                }

                it.favoriting_count.let {
                    list[2].title = "喜欢" + CommonUtils.formatCount(it)
                }

                mUserVideoAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun resetInfo() {
        hasMoreAwe = true
        maxAweCursor = "0"
        aweList.clear()
        aweAdapter.notifyDataSetChanged()
    }

    override fun afterView() {

        arguments?.let {
            val uid = it.getString("uid")
            if (!uid.isNullOrBlank()) {
                userViewModel.queryUser(uid)
            }
        }

    }

    override fun layoutId() = R.layout.fragment_user
}
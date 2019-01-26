package com.zl.douyin.ui.user

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.zl.core.base.ModeFragment
import com.zl.core.extend.onScrollState
import com.zl.core.utils.CommonUtils
import com.zl.core.utils.DateUtils
import com.zl.core.utils.DisplayUtils
import com.zl.core.utils.GlideUtils
import com.zl.core.view.AppBarStateChangeListener
import com.zl.core.view.GridSpacingItemDecoration
import com.zl.douyin.R
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

    private var lastUserId: String? = null
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
        list.add(TitleView("喜欢0", getFavoriteView()))
        mUserVideoAdapter = UserVideoAdapter(list)
        viewPager.adapter = mUserVideoAdapter
        tabLayout.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                Log.i(TAG, "onPageSelected: $position")
            }

        })
    }

    //这个控制的数据最好放到ViewModel里面
    private var hasMoreAwe: Boolean = true
    private var maxAweCursor: String = "0"
    private var aweList: MutableList<FeedItem> = mutableListOf()
    private lateinit var aweAdapter: VideoGridAdapter

    private fun getAweView(): View {

        aweAdapter = VideoGridAdapter(aweList) {

        }

        return RecyclerView(activity!!).apply {
            layoutManager = GridLayoutManager(activity, 3)
            addItemDecoration(GridSpacingItemDecoration(spanCount = 3, space = DisplayUtils.dp2px(activity!!, 1f).toInt(), includeEdge = false, color = Color.BLACK))
            adapter = aweAdapter

            onScrollState {
                when (it) {
                    RecyclerView.SCROLL_STATE_IDLE -> { //当屏幕停止滚动

                        val layoutManager = layoutManager as GridLayoutManager
                        val first = layoutManager.findFirstVisibleItemPosition()
                        val last = layoutManager.findLastVisibleItemPosition()

                        if ((last + layoutManager.spanCount + 3) > list.size && hasMoreAwe && userViewModel.isLoading.value != true) {
                            userEntity?.uid?.let {
                                userViewModel.queryAwe(it, maxAweCursor)
                            }
                        }

//                        val i = (first + last)/2
//                        val holder = findViewHolderForAdapterPosition(i)
//                        if (holder != null) {
//                            val drawable = holder.itemView.dynamicCoverImg.drawable
//                            if (drawable is WebpDrawable) {
//                                drawable.setLoopCount(0) //最中间的动图播放
//                            }
//                        }
                    }

                }
            }
        }
    }

    private fun getDongtaiView(): View {
        return RecyclerView(activity!!)
    }

    private var hasMoreFavorite: Boolean = true
    private var maxFavoriteCursor: String = "0"
    private var favoriteList: MutableList<FeedItem> = mutableListOf()
    private lateinit var favoriteAdapter: VideoGridAdapter

    private fun getFavoriteView(): View {

        favoriteAdapter = VideoGridAdapter(favoriteList) {

        }

        return RecyclerView(activity!!).apply {
            layoutManager = GridLayoutManager(activity, 3)
            addItemDecoration(GridSpacingItemDecoration(spanCount = 3, space = DisplayUtils.dp2px(activity!!, 1f).toInt(), includeEdge = false, color = Color.BLACK))
            adapter = favoriteAdapter

            onScrollState {
                when (it) {
                    RecyclerView.SCROLL_STATE_IDLE -> { //当屏幕停止滚动
                        val layoutManager = layoutManager as GridLayoutManager
                        val last = layoutManager.findLastVisibleItemPosition()

                        if ((last + layoutManager.spanCount + 3) > list.size && hasMoreFavorite && userViewModel.isLoading.value != true) {
                            userEntity?.uid?.let {
                                userViewModel.queryFavorite(it, maxFavoriteCursor)
                            }
                        }
                    }
                }
            }
        }
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
        Log.i(TAG, "observe: ")
        userViewModel = ViewModelProviders.of(this, UserViewModel.Factory(UserRepository.get())).get(UserViewModel::class.java)
        shareViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        //当滑到第一个fragment时会销毁这个fragment，再划回来会导致这些livedata重复被观察,解决办法是观察完了赋值null
        shareViewModel.currentSelectUser.observe(this, Observer {
            if (it != null) {
                userViewModel.userInfo.postValue(it)
                shareViewModel.currentSelectUser.value = null
            }
        })

        shareViewModel.queryUser.removeObservers(activity!!)
        shareViewModel.queryUser.observe(this, Observer { value ->
            if (value != null && value) {
                userEntity?.uid?.let {
                    if (lastUserId != it) {
                        resetInfo()
                        lastUserId = it
                        userViewModel.queryUser(it)
                        userViewModel.queryAwe(it, maxAweCursor)
                    }
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
                userViewModel.moreAweVideoList.value = null
            }
        })

        userViewModel.hasMoreFavorite.observe(this, Observer {
            hasMoreFavorite = it ?: true
        })

        userViewModel.maxFavoriteCursor.observe(this, Observer {
            maxFavoriteCursor = it ?: "0"
        })

        userViewModel.moreFavoriteVideoList.observe(this, Observer {
            if (it != null) {
                favoriteList.addAll(it)
                favoriteAdapter.notifyDataSetChanged()
                userViewModel.moreFavoriteVideoList.value = null
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

        val behavior = (appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior
        if (behavior is AppBarLayout.Behavior) {
            val appBarLayoutBehavior = behavior as AppBarLayout.Behavior?
            val topAndBottomOffset = appBarLayoutBehavior!!.topAndBottomOffset
            if (topAndBottomOffset != 0) {
                appBarLayoutBehavior.topAndBottomOffset = 0
            }
        }

        hasMoreAwe = true
        maxAweCursor = "0"
        aweList.clear()
        aweAdapter.notifyDataSetChanged()

        hasMoreFavorite = true
        maxFavoriteCursor = "0"
        favoriteList.clear()
        favoriteAdapter.notifyDataSetChanged()
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
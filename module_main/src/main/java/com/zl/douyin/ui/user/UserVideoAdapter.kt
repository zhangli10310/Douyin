package com.zl.douyin.ui.user

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/19 16:12.<br/>
 */
class UserVideoAdapter(var list: MutableList<TitleView>): PagerAdapter() {


    override fun getCount() = list.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = list[position].view
        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return list[position].title
    }

}
package com.zl.core.view

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View


/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/2/7 11:47.<br/>
 */
class GridSpacingItemDecoration(private var spanCount: Int, private var space: Int = 10, private var includeEdge: Boolean = true) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {

        //这里是关键，需要根据你有几列来判断
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left = space - column * space / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * space / spanCount // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = space
            }
            outRect.bottom = space // item bottom
        } else {
            outRect.left = column * space / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right = space - (column + 1) * space / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = space // item top
            }
        }
    }
}
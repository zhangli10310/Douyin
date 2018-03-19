package com.zl.core.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View


/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/2/7 11:47.<br/>
 */
class GridSpacingItemDecoration(private var spanCount: Int, private var space: Int = 10, private var includeEdge: Boolean = true, color: Int = Color.WHITE) : RecyclerView.ItemDecoration() {

    private var dividerPaint: Paint = Paint()

    init {
        //设置分割线颜色
        dividerPaint.color = color
    }

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

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        //得到列表所有的条目
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val left = view.left.toFloat()
            val top = view.top.toFloat()
            val right = view.right.toFloat()
            val bottom = view.bottom.toFloat()
            if (includeEdge) {
                c.drawRect(left, bottom, right+space, bottom + space, dividerPaint) //底部
                c.drawRect(right, top-space, right + space, bottom, dividerPaint) //右部
                if (i % spanCount == 0) { //第一列
                    c.drawRect(left - space, top, left, bottom + space, dividerPaint) //左部
                }
                if (i / spanCount == 0) { //第一行
                    c.drawRect(left - space, top - space, right, top, dividerPaint) //顶部
                }
            } else {
                if (i % spanCount != 0) { //非第一列
                    c.drawRect(left - space, top, left, bottom, dividerPaint) //左部
                }
                if (i / spanCount != 0) { //非第一行
                    if (i % spanCount != 0) { //非第一列
                        c.drawRect(left - space, top - space, right, top, dividerPaint) //顶部
                    } else {
                        c.drawRect(left, top - space, right, top, dividerPaint) //顶部
                    }
                }
            }

        }
    }
}
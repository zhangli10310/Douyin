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
 * Created by zhangli on 2018/2/6 19:21.<br/>
 */
class LinearItemDecoration(private val space: Int = 5, color: Int = Color.WHITE) : RecyclerView.ItemDecoration() {

    private var dividerPaint: Paint = Paint()

    init {
        //设置分割线颜色
        dividerPaint.color = color
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        if ((view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition < parent.childCount) {
            outRect.bottom = space
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        //得到列表所有的条目
        val childCount = parent.childCount
        //得到条目的宽和高
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (i in 0 until childCount - 1) {
            val view = parent.getChildAt(i)
            //计算每一个条目的顶点和底部 float值
            val top = view.bottom.toFloat()
            val bottom = view.bottom + space
            //重新绘制
            c.drawRect(left.toFloat(), top, right.toFloat(), bottom.toFloat(), dividerPaint)
        }
    }
}
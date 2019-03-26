package com.zl.douyin.ui.user

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout


class BackgroundImageBehavior : CoordinatorLayout.Behavior<ImageView> {

    private val TAG = "BackgroundImageBehavior"

    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ImageView, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: ImageView, layoutDirection: Int): Boolean {
        Log.i(TAG, "onLayoutChild: ")
        return true
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ImageView, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior
            if (behavior is AppBarLayout.Behavior) {
                val topAndBottomOffset = behavior.topAndBottomOffset
                Log.i(TAG, "onDependentViewChanged: $topAndBottomOffset")
//                child.offsetTopAndBottom(topAndBottomOffset)
            }
        }
        return true
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: ImageView, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        Log.i(TAG, "onStartNestedScroll: ${child.top}")
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

}
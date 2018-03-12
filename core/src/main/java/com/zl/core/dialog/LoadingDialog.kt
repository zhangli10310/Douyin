package com.zl.core.dialog

import android.app.Dialog
import android.content.Context
import com.zl.core.drawable.render.MaterialLoadingRenderer
import com.zl.core.view.LoadingView
import com.zl.core.R

/**
 * Created by zhangli
 */
class LoadingDialog @JvmOverloads constructor(context: Context, themeResId: Int = R.style.LoadingDialog) : Dialog(context, themeResId) {

    private val mLoadingView: LoadingView = LoadingView(context)

    init {
        mLoadingView.setPadding(20, 20, 20, 20)
        mLoadingView.setLoadingRenderer(MaterialLoadingRenderer(context))
        setContentView(mLoadingView)
    }
}

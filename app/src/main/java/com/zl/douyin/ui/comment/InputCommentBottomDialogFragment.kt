package com.zl.douyin.ui.comment

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialogFragment
import android.view.*
import com.zl.douyin.R

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/9/21 14:32.<br/>
 * Copyright (c) 2015年 Beijing Yunshan Information Technology Co., Ltd. All rights reserved.<br/>
 */
class InputCommentBottomDialogFragment : AppCompatDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return BaseBottomSheetDialog(context!!, theme)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (dialog != null && dialog.window != null) {
//            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.window!!.attributes.windowAnimations = R.style.dialogAnim
            dialog.window.setGravity(Gravity.BOTTOM)
            dialog.window.decorView.setPadding(0, 0, 0, 0)

            val lp = dialog.window.attributes
            //设置窗口宽度为充满全屏
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            //设置窗口高度为包裹内容
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //将设置好的属性set回去
            dialog.window.attributes = lp

        }
        return inflater.inflate(R.layout.layout_input_comment, container, false)
    }

}
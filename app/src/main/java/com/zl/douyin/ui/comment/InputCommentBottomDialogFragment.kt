package com.zl.douyin.ui.comment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatDialogFragment
import android.view.*
import com.zl.core.view.BaseBottomSheetDialog
import com.zl.douyin.R

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/9/21 14:32.<br/>
 */
class InputCommentBottomDialogFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BaseBottomSheetDialog(context!!, theme)
        dialog.heightDP = ViewGroup.LayoutParams.WRAP_CONTENT
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_input_comment, container, false)
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        super.show(manager, tag)


    }

}
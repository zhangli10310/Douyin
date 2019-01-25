package com.zl.douyin.ui.comment

import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDialogFragment
import com.zl.core.view.BaseBottomSheetDialog
import com.zl.douyin.R
import kotlinx.android.synthetic.main.layout_input_comment.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/9/21 14:32.<br/>
 */
class InputCommentBottomDialogFragment : AppCompatDialogFragment() {

    private var rootBottom = Integer.MIN_VALUE
    private var rootMid = Integer.MAX_VALUE

    private var listener: ViewTreeObserver.OnGlobalLayoutListener

    init {
        listener = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            dialog?.window?.decorView?.getGlobalVisibleRect(r)
//            Log.i("jkl", "${r.bottom},$rootBottom,$rootMid")
            if (rootBottom == Integer.MIN_VALUE) {
                rootBottom = r.bottom
                return@OnGlobalLayoutListener
            }
            // adjustResize，软键盘弹出后高度会变小
            if (r.bottom < rootBottom) {
                rootMid = r.bottom
            }

            if (r.bottom > rootMid) {
                dismiss()
            }

        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BaseBottomSheetDialog(context!!, theme)
        dialog.heightDP = ViewGroup.LayoutParams.WRAP_CONTENT
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_input_comment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener(listener)
        commentEdit.onKeyBoardHideListener = {
            dismiss()
        }

        commentEdit.post {
            commentEdit.requestFocus()
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(commentEdit, 0)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            dialog?.window?.decorView?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
        }
    }

}
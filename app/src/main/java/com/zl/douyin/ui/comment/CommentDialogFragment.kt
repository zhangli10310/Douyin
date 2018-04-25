package com.zl.douyin.ui.comment

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zl.core.view.BaseBottomSheetDialog
import com.zl.douyin.R
import kotlinx.android.synthetic.main.fragment_comment.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/4/24 19:32.<br/>
 */
class CommentDialogFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BaseBottomSheetDialog(context!!, theme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        if (dialog != null && dialog.window != null) {
//            dialog.window!!.attributes.windowAnimations = R.style.dialogAnim
//        }
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        closeImg.setOnClickListener {
            dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(activity!!)
        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = TextView(parent.context)
                view.textSize = 18f
                return object : RecyclerView.ViewHolder(view) {

                }
            }

            override fun getItemCount() = 50

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as TextView).text = "jjjjj"
            }

        }
    }
}
package com.zl.douyin.ui.comment

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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

    private lateinit var viewModel: CommentViewModel

    private var awemeId = 0L
    private var list = mutableListOf<CommentItem>()
    private lateinit var mAdapter: CommentAdapter

    companion object {

        const val AWEME_ID = "aweme_id"

        @Synchronized
        fun getInstance(awemeId: Long): CommentDialogFragment {
            return instance!!.apply {
                arguments = Bundle().apply {
                    putLong(AWEME_ID, awemeId)
                }
            }
        }

        private var instance: CommentDialogFragment? = null
            get() {
                if (field == null) {
                    field = CommentDialogFragment()
                }
                return field
            }
    }

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
        observer()


        closeImg.setOnClickListener {
            dismiss()
        }

        mAdapter = CommentAdapter(list)
        recyclerView.layoutManager = LinearLayoutManager(activity!!)
        recyclerView.adapter = mAdapter

        arguments?.let {
            val id = it.getLong(AWEME_ID)
            if (id != awemeId) {
                awemeId = id
                list.clear()
                loadComment()
            }
        }
    }

    private fun loadComment() {
        viewModel.loadComment(awemeId, list.size)
    }

    private fun observer() {
        viewModel = ViewModelProviders.of(this, CommentViewModel.Factory(CommentRepository.get())).get(CommentViewModel::class.java)

        viewModel.moreCommentList.observe(this, Observer {
            it?.apply {
                titleText.text = "${it.total}条评论"
            }?.comments?.let {
                list.addAll(it)
            }
        })

        viewModel.hasMore.observe(this, Observer {
            progressBar.visibility = View.GONE
        })
    }
}
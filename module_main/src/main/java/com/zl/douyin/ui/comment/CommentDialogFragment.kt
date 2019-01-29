package com.zl.douyin.ui.comment

import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private var list = mutableListOf<CommentItem>()
    private lateinit var mAdapter: CommentAdapter

    companion object {

        const val AWEME_ID = "aweme_id"

        fun newInstance(awemeId: Long): CommentDialogFragment {
            return CommentDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(AWEME_ID, awemeId)
                }
            }
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetEdit)
//    }

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
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
//        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        observer()

        closeImg.setOnClickListener {
            dismiss()
        }

        commentEdit.setOnClickListener {
//            startActivity(Intent(activity, InputCommentBottomDialogFragment::class.java))
            val fragment = InputCommentBottomDialogFragment()
            fragment.show(fragmentManager!!, "tag")
        }

        mAdapter = CommentAdapter(list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = mAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> { //当屏幕停止滚动

                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val first = layoutManager.findFirstVisibleItemPosition()
                        val last = layoutManager.findLastVisibleItemPosition()

                        if ((last + 3) > list.size) {
                            loadComment()
                        }
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {//当屏幕滚动且用户使用的触碰或手指还在屏幕上

                    }

                    RecyclerView.SCROLL_STATE_SETTLING -> {//由于用户的操作，屏幕产生惯性滑动

                    }

                }
            }
        })

        arguments?.let {
            val id = it.getLong(AWEME_ID)
            if (id != viewModel.awemeId) {
                viewModel.awemeId = id
                viewModel.reset()
                loadComment()
            }
        }

    }

    private fun loadComment() {
        viewModel.loadComment()
    }

    private fun observer() {
        viewModel = ViewModelProviders.of(activity!!, CommentViewModel.Factory(CommentRepository.get())).get(CommentViewModel::class.java)

        viewModel.lastComment.observe(this, Observer {
            handleView(it)
        })

        viewModel.allCommentList.observe(this, Observer {
            list.clear()
            if (it != null) {
                list.addAll(it)
            }
            mAdapter.notifyDataSetChanged()
        })
    }

    private fun handleView(data: CommentData?) {
        if (data != null) {
            titleText.text = "${data.total}条评论"
            if (progressBar.visibility == View.VISIBLE) {
                progressBar.visibility = View.GONE
            }
        } else {
            titleText.text = "0条评论"
            progressBar.visibility = View.VISIBLE
        }
    }


}
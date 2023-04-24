package com.example.proxtalk.commentsActivity

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.proxtalk.User
import com.lukas.proxtalk.R


class CommentsAdapter(
    var comments: MutableList<CommentItem> = mutableListOf()
): RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {
    class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var constraint:  ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.constraint_layout)
        var container: LinearLayout = itemView.findViewById<LinearLayout>(R.id.commentContainer2)
        var username: TextView = itemView.findViewById<TextView>(R.id.commentUsername)
        var text: TextView = itemView.findViewById<TextView>(R.id.commentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsAdapter.CommentsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.comment_item, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        var comment = comments[position]
        if(comment.username != null){
            holder.username.text = comment.username
        }else{
            holder.username.text = ""
            holder.username.visibility = View.GONE
            val constraintSet = ConstraintSet()
            constraintSet.clone(holder.constraint)
            constraintSet.clear(R.id.commentContainer2,ConstraintSet.START)
            constraintSet.clear(R.id.commentContainer2,ConstraintSet.END)
            constraintSet.connect(R.id.commentContainer2,ConstraintSet.END,R.id.constraint_layout,ConstraintSet.END,20)
            constraintSet.connect(R.id.commentContainer2,ConstraintSet.START,R.id.constraint_layout,ConstraintSet.START,80)
            holder.container.gravity = Gravity.END
            constraintSet.applyTo(holder.constraint)
        }
        holder.text.text = comment.text
    }

    fun addComment(comment: CommentItem,id:Int){
        comments.add(comment)
        if(id == User.commentsSelected){
            notifyItemInserted(itemCount-1)
        }

    }
}


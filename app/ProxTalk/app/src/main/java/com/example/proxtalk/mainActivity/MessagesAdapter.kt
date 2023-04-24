package com.example.proxtalk.mainActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.User
import com.example.proxtalk.commentsActivity.CommentsActivity
import com.example.proxtalk.network.APICalls
import com.example.proxtalk.network.RequestsBase
import com.lukas.proxtalk.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class MessagesAdapter(
    var messages: MutableList<MessageItem> = mutableListOf()

): RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {
    val messagesViews: MutableMap<Int,MessagesViewHolder> = mutableMapOf<Int,MessagesViewHolder>()
    class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val msgText: TextView = itemView.findViewById(R.id.msgText)
        val username: TextView = itemView.findViewById(R.id.msg_username)
        var imageView: ImageView = itemView.findViewById(R.id.imageViewMsg)
        val imageContainer: LinearLayout = itemView.findViewById(R.id.imageContainer)
        val profileImage: ImageView = itemView.findViewById<ImageView>(R.id.user_profile_image)
        val addReactionBtn: ImageButton = itemView.findViewById<ImageButton>(R.id.addReactionBtn)
        val likeCountText: TextView = itemView.findViewById<TextView>(R.id.likeCount)
        val imageProgress: ProgressBar = itemView.findViewById<ProgressBar>(R.id.imageProgress)
        val commentsBtn: ImageButton = itemView.findViewById<ImageButton>(R.id.sendCommentBtn)
        val commentsCount: TextView = itemView.findViewById<TextView>(R.id.commentsCount)
    }

    fun addMessage(messageItem: MessageItem){
        /**
         * Method to add item and refresh recycler view
         */
        messages.add(0,messageItem)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.view_item,parent,false))
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        /**
         * Method of initializing recycler view item
         * Setting message content, download profile picture from server
         * Optionally download message content image
         * Set onClick listener on reaction button
         */
        var message = messages[position]
        holder.msgText.text = message.messageText
        holder.username.text = message.username
        holder.likeCountText.text = message.reactionCount.toString()
        if(message.image)
        {
            Log.i(TAG,"IMAGE VIEW")
            holder.imageProgress.isVisible = true
            loadImage(holder.imageView, message.id!!,holder.imageProgress,holder.imageContainer)
        }
        else{
            holder.imageContainer.isVisible = false
//            holder.imageProgress.isVisible = false
        }
        if(message.id != null){
            Picasso.with(User.act.application)
                .load(RequestsBase.urlBase+"/message/profile_image?token="+User.token+"&id="+ message.id)
                .into(holder.profileImage,object: com.squareup.picasso.Callback {
                    override fun onSuccess() {
                    }
                    override fun onError() {
                        holder.profileImage.setImageDrawable(ContextCompat.getDrawable(User.act.application, R.drawable.app_logo))
                    }
                })
            holder.addReactionBtn.setOnClickListener {
                holder.addReactionBtn.isEnabled = false
                User.act.lifecycleScope.launch(Dispatchers.IO) {
                    var api = APICalls()
                    var res = api.addMessageReaction(message.id!!,1)
                    if(res.second == 200){
                        var json = JSONObject(res.first)
                        var state = json["state"] as Boolean
                        if(state){
                            Log.i(TAG,"State changed")
                            holder.addReactionBtn.setImageResource(R.drawable.like_on)
                        }else{
                            holder.addReactionBtn.setImageResource(R.drawable.like_off)
                        }
                    }
                    else{
                        User.act.makeToast(User.act.getString(R.string.cant_do_action))
                    }
                    User.act.runOnUiThread {
                        holder.addReactionBtn.isEnabled = true
                    }
                }
            }
            holder.commentsBtn.setOnClickListener {
                val intent = Intent(User.act, CommentsActivity::class.java)
                val b = Bundle()
                b.putInt("id", message.id!!)
                intent.putExtras(b)

                User.act.startActivity(intent)
            }
            messagesViews[message.id!!] = holder
        }else{
            holder.profileImage.setImageDrawable(ContextCompat.getDrawable(User.act.application, R.drawable.app_logo))
        }
    }

    fun loadImage(img: ImageView,id: Int,progress:ProgressBar,layout:LinearLayout){
        /**
         * Method to download load message image from server
         * If downloading failed it will call recursively this function to try again downloading
         */
        Picasso.with(User.act.application)
            .load(RequestsBase.urlBase+"/message/image?token="+User.token+"&id="+ id)
            .into(img,object: com.squareup.picasso.Callback {
                override fun onSuccess() {
                    progress.isVisible = false
                    layout.isVisible = true
                }
                override fun onError() {
                    progress.isVisible = true
                    loadImage(img,id,progress,layout)
                }
            })
    }

    override fun getItemCount(): Int {
        return messages.size
    }

}
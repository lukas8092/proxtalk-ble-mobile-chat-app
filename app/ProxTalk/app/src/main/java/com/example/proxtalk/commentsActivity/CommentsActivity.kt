package com.example.proxtalk.commentsActivity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.User
import com.example.proxtalk.mainActivity.Device
import com.example.proxtalk.mainActivity.MessagesAdapter
import com.example.proxtalk.network.APICalls
import com.lukas.proxtalk.R
import com.lukas.proxtalk.databinding.ActivityCommentsBinding

class CommentsActivity : AppCompatActivity() {
    var messageId: Int = -1
    lateinit var binding: ActivityCommentsBinding
    lateinit var commentsAdapter: CommentsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        User.actComments = this
        binding.toolbarComments.setOnClickListener {
            super.onBackPressed()
        }

        val b = intent.extras
        if (b != null) messageId = b.getInt("id")


        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        for(item in Device.devicesMessages.values){
            if(item.id == messageId){
                commentsAdapter = item.commentsAdapter
                binding.commentsRecyclerView.adapter = item.commentsAdapter
                binding.toolbarComments.title = "Comments at "+ item.username
                User.commentsSelected = item.id!!
            }
        }

        binding.sendCommentBtn.setOnClickListener {
            Log.i(TAG,"Count"+ commentsAdapter.itemCount)
            for(item in Device.devicesMessages.values){
                if(item.id == messageId){
                    if(binding.commentText.text.toString() != ""){
                        var api = APICalls()
                        var res = api.creteComment(messageId,binding.commentText.text.toString())
                        if(res.second == 200){
                            runOnUiThread {
                                commentsAdapter.addComment(CommentItem(null,binding.commentText.text.toString()),messageId)
                            }
                            binding.commentText.text!!.clear()
                            binding.commentsRecyclerView.scrollToPosition(item.commentsAdapter.itemCount-1)
                        }else{
                            Toast.makeText(this@CommentsActivity, "Cant send comment", Toast.LENGTH_SHORT).show() // TODO STrings text add
                        }
                    }
                }
            }
            Log.i(TAG,"Count2"+ commentsAdapter.itemCount)
        }
    }
}
package com.example.proxtalk.mainActivity
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proxtalk.commentsActivity.CommentsAdapter
import java.time.LocalDateTime

data class MessageItem @RequiresApi(Build.VERSION_CODES.O) constructor(
    var messageText: String?,
    var id: Int? = null,
    var username: String? = "ProxTalk",
    var image: Boolean = false,
    var reactionCount: Int = 0,
    var time: LocalDateTime = LocalDateTime.now(),
    var commentsAdapter: CommentsAdapter = CommentsAdapter()
){
    fun createString(): String {
        /**
         * Method to create string that contains message information
         * This string is advertised in characteristic
         */
        var content = messageText?.replace(Device.delimiter,"|")
        var imgVal = "0"
        if(image){
            imgVal = "1"
        }
        return "px${Device.delimiter}${id}${Device.delimiter}${username}${Device.delimiter}${imgVal}${Device.delimiter}${content}"
    }
}
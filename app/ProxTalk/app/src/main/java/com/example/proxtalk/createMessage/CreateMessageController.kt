package com.example.proxtalk.createMessage

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.BLE.BtState
import com.example.proxtalk.Permissions
import com.example.proxtalk.User
import com.example.proxtalk.network.APICalls
import com.example.proxtalk.mainActivity.MessageItem
import com.lukas.proxtalk.R
import java.time.LocalDateTime

class CreateMessageController(
    var activity: CreateMessageActivity
) {
    var model: CreateMessageModel = CreateMessageModel(this)

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(){
        /**
         * Method where is validated message content from user
         * and sent to API
         */
        model.messageText = activity.messageText.text.toString()
        if(model.validateMessageText()){
            activity.runOnUiThread{
                activity.messageTextLayout.error = null
            }
            var api = APICalls()
            var msg = MessageItem(model.messageText,username=User.username,time= LocalDateTime.now())
            if(activity.imageFile != null){
                msg.image = true
            }
            var res = api.createMessage(msg)
            when (res.second) {
                200 -> {
                    Permissions.requesting = BtState.BOTH
                    msg.id = res.first
                    if(activity.imageFile != null){
                        Log.i(TAG,"Image sent")
                        var res = api.postMessageImage(activity.application, activity.imageFile!!, msg.id!!)
                        msg.image = true
                        if(res != 200){
                            Toast.makeText(activity.application, activity.getString(R.string.image_not_sent), Toast.LENGTH_LONG).show()
                        }
                        activity.imageFile = null
                    }
                    User.message = msg
                    activity.runOnUiThread {
                        User.act.controller.addLocalMessage(msg)
                        Permissions.requesting = BtState.BOTH
                        User.sendedStat++
                        activity.goToMainActivity()
                    }
                }
                0 -> {
                    activity.runOnUiThread {
                        Toast.makeText(activity.application, activity.getString(R.string.cant_reach_server), Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    activity.runOnUiThread {
                        activity.messageTextLayout.error = activity.getString(R.string.cant_sent_msg)
                    }
                }
            }
        }else{
            activity.runOnUiThread {
                activity.messageTextLayout.error = activity.getString(R.string.empty_content)
            }
        }
    }
}
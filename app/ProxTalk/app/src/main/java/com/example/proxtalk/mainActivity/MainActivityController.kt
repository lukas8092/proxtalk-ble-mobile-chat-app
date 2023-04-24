package com.example.proxtalk.mainActivity

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.proxtalk.BLE.UUIDS
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.network.APICalls
import com.example.proxtalk.notifications.Notifications
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityController(
    var activity: MainActivity
) {

    fun updateReactions(id: Int, num: String){
        /**
         * Method that will update number of reaction in recycler view item
         */
        var view = activity.messagesAdapter.messagesViews[id]
        Log.i(UUIDS.TAG, view!!.username.text.toString())
        activity.runOnUiThread{
            view.likeCountText.text = num
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addMessage(text: String,address: String){
        /**
         * Method that will check if incoming message is valid and check if message is already in recycler view
         * If yes it will add it to message list with mac address and add it to recycler view
         * Subscribe message id to websocket, call api endpoint message received
         */
        val msg = Device.validateMessage(text)
        if(msg != null){
            if(Device.devicesMessages.containsKey(address)){
                if(msg.id == Device.devicesMessages[address]?.id){
                    Device.devicesMessages[address] = msg
                    return
                }
            }
            for(existingMsg in activity.messagesAdapter.messages){
                if (existingMsg.id == msg.id){
                    return
                }
            }
            Device.devicesMessages[address] = msg
            activity.runOnUiThread{
                activity.messagesAdapter.addMessage(msg)
                activity.messagesView.scrollToPosition(0)
            }
            activity.ws?.subsribeToMessageReactions(Device.devicesMessages[address]?.id!!)
            Notifications.sendNotificationReceived()
            var api = APICalls()
            api.addReceivedMessage(msg.id!!)
        }
    }

    fun addLocalMessage(msg: MessageItem){
        /***
         * Message to add message to recycler view that was created on user device
         * Subscribe to websocket
         */
        Device.devicesMessages[msg.id.toString()] = msg
        activity.runOnUiThread{
            activity.messagesAdapter.addMessage(msg)
            activity.messagesView.scrollToPosition(0)
        }
        activity.ws?.subsribeToMessageReactions(msg.id!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRandomMessage(){
        /**
         * Method that will call random message endpoint
         * And add it to recycler view
         */
        var api = APICalls()
        activity.lifecycleScope.launch(Dispatchers.IO){
            var res = api.getRandomMessage()
            if(res.first == 200 && res.second != null){
                addLocalMessage(res.second!!)
            }
        }
    }


}
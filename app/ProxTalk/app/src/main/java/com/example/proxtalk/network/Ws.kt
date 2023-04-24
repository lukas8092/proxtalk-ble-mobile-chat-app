package com.example.proxtalk.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.proxtalk.Animations
import com.example.proxtalk.BLE.UUIDS
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.User
import com.example.proxtalk.commentsActivity.CommentItem
import com.example.proxtalk.mainActivity.Device
import com.example.proxtalk.mainActivity.MessageItem
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject


class Ws: WebSocketListener() {
    /**
     * Class where is logic of websocket
     */
    var ws: WebSocket? = null
    var isConnected: Boolean = false
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.i(TAG,"Ws opened")
        ws = webSocket
        isConnected = true
        for(msg in Device.devicesMessages.values){
            Log.i(TAG,"Subscribed to "+ msg.id)
            subsribeToMessageReactions(msg.id!!)
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        /**
         * Callback when message arrive, it will update UI with received value
         */
        super.onMessage(webSocket, text)
        Log.i(TAG, "Ws receive: $text")
        try{
            var json = JSONObject(text)
            if(json["method"] == "updated_reactions") {
                var data = JSONObject(json["data"].toString())
                User.act.controller.updateReactions(json["id"] as Int,data["1"].toString())
            }
            if(json["method"] == "add_comment"){
                var messageId = json["id"] as Int
                for(item in Device.devicesMessages.values){
                    if(messageId == item.id){
                        var commentData = JSONObject(json["data"].toString())
                        var view = User.act.messagesAdapter.messagesViews[messageId]
                        if(commentData["username"].toString() != User.username){
                            if(User.actComments != null){
                                User.actComments!!.runOnUiThread {
                                    item.commentsAdapter.addComment(CommentItem(commentData["username"].toString(),commentData["comment"].toString()),messageId)
                                }
                            }
                            else{
                                User.act.runOnUiThread {
                                    item.commentsAdapter.addComment(CommentItem(commentData["username"].toString(),commentData["comment"].toString()),messageId)
                                }
                            }
                        }
                        User.act.runOnUiThread {
                            view!!.commentsCount.text = item.commentsAdapter.itemCount.toString()
                        }
                    }
                }
            }
        }catch(e: java.lang.Exception){
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        isConnected = false
        Thread.sleep(4_000)
        Log.i(TAG, "Ws reconnecting with error $t")
        Device.openWebsocket()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        isConnected = false
        Thread.sleep(4_000)
        Log.i(TAG,"Ws reconnecting")
        Device.openWebsocket()
    }

    fun send(data: String){
        ws?.send("{ \"subscribe_to_message\":{ \"id\": 302 } }")
    }

    fun subsribeToMessageReactions(id: Int){
        /**
         * Method to send json string to websocket to make subscription to message
         */
        if(!isConnected){
            return
        }
        while(ws == null){
        }
        var json = JSONObject()
        var idJson = JSONObject()
        idJson.put("id",id)
        json.put("subscribe_to_message",idJson)
        ws!!.send(json.toString())
    }
}

package com.example.proxtalk.mainActivity

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proxtalk.User
import com.example.proxtalk.network.RequestsBase
import com.example.proxtalk.network.Ws
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.streams.asSequence


object Device {
    var isOnBackground: Boolean = false
    val scanDeviceDelay: Long = 5
    var devicesMessages: MutableMap<String,MessageItem> = mutableMapOf<String,MessageItem>()
    var allowedImageExtension = arrayOf("jpg","png","jpeg")
    var delimiter = "│"

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateMessage(value: String): MessageItem? {
        /**
         * Method that will validate and parse incoming message
         * and return messageItem object
         */
        try {
            var parts = value.split(delimiter)
            if (parts[0] == "px" && parts.size == 5){
                var img = false
                if (parts[3] == "1") {
                    img = true
                }
                return MessageItem(parts[4], parts[1].toInt(), parts[2], img)
            }
        }catch(e:java.lang.Exception){
        }
        return null
    }


    fun openWebsocket(){
        /**
         * Method to open websocket connection
         */
        var client = OkHttpClient()
        var request = Request.Builder().url(RequestsBase.urlBaseWs).build()
        var listener = Ws()
        client.newWebSocket(request, listener)
        client.dispatcher.executorService.shutdown()
        User.act.ws = listener
    }

    fun generatePassword(): String {
        val source = "abcdefghcjkllmnopgxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789.@*-"
        var passwd = java.util.Random().ints(30, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
        return passwd
    }

}
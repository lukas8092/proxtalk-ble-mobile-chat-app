package com.example.proxtalk.network

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.User
import com.example.proxtalk.lib.URIPathHelper
import com.example.proxtalk.mainActivity.MessageItem
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.File

class APICalls {
    /**
     * Class for api calls
     * Every API endpoint is documented in swagger documentation
     * Look into that for more information's
     */
    var requests: Requests = Requests()
    fun login(username: String, password: String,init:Boolean=false): Pair<String?, Int?> {
        /**
         * Calling endpoint to get token from credentials
         * @return status code and body of response which is token
         */
        var res = requests.Get("user/login",init,username,password)
        if(res.second == 200){
            var json = JSONObject(res.first)
            return Pair(json["token"].toString(),res.second)
        }
        return res
    }

    fun registration(json: JSONObject): Pair<String?, Int?> {
        /**
         * Calling endpoint to register new user
         */
        var res = requests.PostWithBody("user/register",json.toString())
        return  res
    }

    fun verifyToken(token: String,init:Boolean=false): Int? {
        /**
         * Calling endpoint to verify validity of token
         */
        var res = requests.Get("user/validate_token?token=$token",init=init)
        return res.second
    }

    fun userStats(): Pair<String?, Int?> {
        return requests.Get("user/stats?token=" + User.token)
    }

    fun createMessage(msg: MessageItem): Pair<Int, Int?> {
        /**
         * Calling endpoint to create message in database
         */
        var json = JSONObject()
        json.put("token", User.token)
        json.put("content",msg.messageText)
        json.put("image",msg.image)
        var res = requests.PostWithBody("message/create",json.toString())
        if(res.second == 200){
            var jsonResponse = JSONObject(res.first)
            return Pair(jsonResponse["id"].toString().toInt(),res.second)
        }
        else return Pair(-1,res.second)
    }

    fun addMessageReaction(messageId: Int,reactionType: Int): Pair<String?, Int?> {
        /**
         * Endpoint to add reaction to specific message
         * Reaction type is always 1, different values are for future use to have more reaction types
         */
        var json = JSONObject()
        json.put("token",User.token)
        json.put("message_id",messageId)
        json.put("reaction_type",reactionType)
        var res = requests.PostWithBody("/message/reaction",json.toString())
        return res
    }

    fun postMessageImage(app:Application,imageUri: Uri,id: Int): Int{
        /**
         * Calling endpoint that will upload image to server
         */
        var imageFile = File(URIPathHelper.getPath(app,imageUri))
        var headers = arrayOf<Pair<String,String>>(Pair("token",User.token),Pair("id",id.toString()))
        var res = requests.PostImage("/message/postImage",imageFile,app,headers)
        Log.i(TAG, "Image sent code:$res")
        return res
    }

    fun postMessageImage(app:Application,imageFile: File,id: Int): Int{
        /**
         * Calling endpoint that will upload image to server
         */
        var headers = arrayOf<Pair<String,String>>(Pair("token",User.token),Pair("id",id.toString()))
        var res = requests.PostImage("/message/postImage",imageFile,app,headers)
        Log.i(TAG, "Image sent code:$res")
        return res
    }

    fun postNewProfileImage(app:Application,imageFile: File): Int{
        /**
         * Calling endpoint that will upload image to server
         */
        var headers = arrayOf<Pair<String,String>>(Pair("token",User.token))
        var res = requests.PostImage("/user/upload_profile_image",imageFile,app,headers)
        Log.i(TAG, "Image sent code:$res")
        return res
    }

    fun addReceivedMessage(message_id: Int): Int? {
        var json = JSONObject()
        json.put("token",User.token)
        json.put("message_id",message_id)
        var res = requests.PostWithBody("/user/received",json.toString())
        if(res.second == 200){
            User.recivedStat++
        }
        return res.second
    }

    fun getHistory(): Pair<String?, Int?> {
        return requests.Get("message/history?token=" + User.token)
    }

    fun changeUsername(newUsername: String): Int? {
        var json = JSONObject()
        json.put("token",User.token)
        json.put("username",newUsername)
        var res = requests.PostWithBody("user/change_username",json.toString())
        return res.second
    }

    fun changePassword(newPassword: String,app:Application): Int? {
        var json = JSONObject()
        json.put("token",User.token)
        json.put("password",newPassword)
        var res = requests.PostWithBody("user/change_password",json.toString())
        if(res.second == 200){
            var data = JSONObject(res.first)
            User.token = data["token"].toString()
            User.saveToken(app)
        }
        return res.second
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRandomMessage(): Pair<Int?, MessageItem?> {
        var res = requests.Get("message/random_message?token="+ User.token)
        if(res.second == 200){
            var json = JSONObject(res.first)
            Log.i(TAG, res.first!!)
            var msg = MessageItem(json["content"] as String?,
                json["id"] as Int?, json["username"] as String?,json["image"] is String)
            Log.i(TAG,json["image"].toString())
            Log.i(TAG,msg.image.toString())
            return Pair(res.second,msg)
        }
        return Pair(res.second,null)
    }

    fun creteComment(message_id: Int,text: String): Pair<String?, Int?> {
        var json = JSONObject()
        json.put("token",User.token)
        json.put("message_id",message_id)
        json.put("comment",text)
        var res = requests.PostWithBody("message/comment",json.toString())
        return res
    }

    fun getAppState(): Pair<Int?, JSONObject?> {
        var res = requests.Get("app/state")
        if(res.second == 200){
            return Pair(res.second,JSONObject(res.first))
        }
        return Pair(res.second,null)
    }
}
package com.example.proxtalk

import android.app.Application
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.proxtalk.BLE.UUIDS
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.commentsActivity.CommentsActivity
import com.example.proxtalk.mainActivity.Device
import com.example.proxtalk.network.APICalls
import com.example.proxtalk.network.RequestsBase
import com.example.proxtalk.mainActivity.MainActivity
import com.example.proxtalk.mainActivity.MessageItem
import com.lukas.proxtalk.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


object User {
    var username: String? = null
    var token: String = ""
    lateinit var act: MainActivity
    var actComments: CommentsActivity? = null
    var commentsSelected: Int = -1
    var message: MessageItem? = null
    var profileImageDrawable: Drawable? = null
    var recivedStat: Int = 0
    var sendedStat: Int = 0

    fun saveToStorage(key:String, value:String?, app:Application){
        /**
         * Method to save value under specific key to local storage of device
         */
        if(value != null){
            val prefs = PreferenceManager.getDefaultSharedPreferences(app)
            val edit: SharedPreferences.Editor = prefs.edit()
            edit.putString(key, value)
            edit.commit()
        }

    }
    fun readFromStorage(key:String,app:Application): String? {
        /**
         * Method to read from local storage of device with key
         */
        val prefs = PreferenceManager.getDefaultSharedPreferences(app)
        return prefs.getString(key, null)
    }

    fun removeDataFromStorage(key: String,app: Application){
        try{
            val prefs = PreferenceManager.getDefaultSharedPreferences(app)
            prefs.edit().remove(key).commit()
        }catch (e: Exception){
        }

    }

    fun saveToken(app:Application){
       saveToStorage("token", token,app)
    }

    fun loadToken(app:Application){
        val tokenStored = readFromStorage("token",app)
        if (tokenStored != null) {
            token = tokenStored
        }
        Log.i(TAG, "Loaded from storage:$token")
    }

    fun saveUsername(app: Application){
        saveToStorage("username", username,app)
    }

    fun loadUsername(app:Application){
        val usernameStored = readFromStorage("username",app)
        username = usernameStored
    }

    fun logout(app:Application){
        username = null
        token = ""
        saveUsername(app)
        saveToken(app)
    }

    fun loadProfilePicture(){
        /**
         * Method to download profile image from server in coroutine
         */
        act.lifecycleScope.launch(Dispatchers.IO) {
            act.runOnUiThread {
                var profileImage = ImageView(act.application)
                Picasso.with(act.application)
                    .load(RequestsBase.urlBase+"/user/profile_image?token="+User.token)
                    .into(profileImage,object: com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            Log.i(UUIDS.TAG,"Loaded")
                            profileImageDrawable = profileImage.drawable
                        }
                        override fun onError() {
                            profileImageDrawable = ContextCompat.getDrawable(act.application, R.drawable.default_profile_picture)
                        }
                    })
            }
        }
    }

    fun getStat(){
        /**
         * Method to get stats about sended and received messages in coroutine
         */
        act.lifecycleScope.launch(Dispatchers.IO) {
            var api = APICalls()
            var res = api.userStats()
            if(res.second == 200){
                var json = JSONObject(res.first)
                sendedStat = json["sended"].toString().toInt()
                recivedStat = json["received"].toString().toInt()
            }
        }
    }

    fun continueAsGuest(app: Application): Pair<String?, String?> {
        var guestUsername = readFromStorage("guest-username",app)
        var guestPassword = readFromStorage("guest-password",app)
        if(guestUsername == null && guestPassword == null){
            var randomUUID = java.util.UUID.randomUUID().toString().substring(0..10)
            guestUsername = "guest-$randomUUID"
            guestPassword = Device.generatePassword()
            var guestEmail = "$randomUUID@gmail.com"
            var api = APICalls()
            var json = JSONObject()
            json.put("username", guestUsername)
            json.put("passwd",guestPassword)
            json.put("email",guestEmail)
            json.put("bt_mac","..")
            var res = api.registration(json)
            if(res.second == 200){
                saveToStorage("guest-username",guestUsername,app)
                saveToStorage("guest-password",guestPassword,app)
            }
        }
        return Pair(guestUsername,guestPassword)
    }



}
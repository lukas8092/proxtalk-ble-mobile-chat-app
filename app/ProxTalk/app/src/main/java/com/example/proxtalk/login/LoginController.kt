package com.example.proxtalk.login


import android.util.Log
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.User
import com.example.proxtalk.network.APICalls
import com.lukas.proxtalk.R

class LoginController(var activity: LoginActivity) {
    var model: LoginModel = LoginModel(this,)

    fun validateInputs(): Boolean {
        val passwd = model.validatePassword()
        val user = model.validateUsername()
        activity.runOnUiThread{
            if (!user){
                activity.binding.logUsername.error = activity.getString(R.string.invalid_username)
            }
            else{
                activity.binding.logUsername.error = null
            }
            if (!passwd){
                activity.binding.logUsername.error = activity.getString(R.string.invalid_password_login)
            }
            else{
                activity.binding.logUsername.error = null
            }
        }
        return user and passwd
    }

    fun loginPressed(username: String, password: String,guest:Boolean=false): Boolean {
        /**
         * Method called after pressing login button
         * which will check if specific login data exists
         */
        model.username = username
        model.password = password
        if(validateInputs()){
            var api = APICalls()
            var res = api.login(model.username,model.password,true)
            if(res.second == 200){
                Log.i(TAG,"Success login")
                User.username = model.username
                User.token = res.first.toString()
                User.saveToken(activity.application)
                User.saveUsername(activity.application)
                if(!guest){
                    User.removeDataFromStorage("guest-username",activity.application)
                    User.removeDataFromStorage("guest-password",activity.application)
                }

                return true
            }
            else if(res.second == 401){
                activity.runOnUiThread {
                    activity.binding.logUsername.error = activity.getString(R.string.bad_login)
                    activity.binding.logPassword.error = null
                }
            }
            else{
                activity.runOnUiThread {
                    activity.binding.logUsername.error = activity.getString(R.string.cant_reach_server)
                    activity.binding.logPassword.error = null
                }
            }
        }
        return false
    }

    fun loginInit(): Boolean{
        /**
         * Method to check if user was already logged in
         * and if saved token is still valid
         */
        User.loadToken(activity.application)
        User.loadUsername(activity.application)
        if(User.token != ""){
            var api = APICalls()
            var res = api.verifyToken(User.token!!,init=true)
            if(res == 200){
                return true
            }
        }
        return false
    }
}
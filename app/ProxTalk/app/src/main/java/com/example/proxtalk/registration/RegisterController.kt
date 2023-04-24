package com.example.proxtalk.registration

import android.util.Log
import android.widget.Toast
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.network.APICalls
import com.lukas.proxtalk.R
import org.json.JSONObject

class RegisterController(
    var activity: RegisterActivity,
) {
    var model: RegisterModel = RegisterModel(this)

    fun registerClick()
    {
        /**
         * Method to check form values
         * If they are correct it will make api call to register
         */
        var check = 0
        model.username = activity.username.text.toString().trim()
        model.email = activity.email.text.toString().trim().lowercase()
        model.password = activity.password.text.toString().trim()
        model.password_check = activity.passwordCheck.text.toString().trim()

        if (model.validateUsername()){
            activity.runOnUiThread {
                activity.usernameLayout.error = null
            }
            check++
        }else{
            activity.runOnUiThread {
                activity.usernameLayout.error = activity.getString(R.string.invalid_username)
            }
        }
        if(model.validateEmail()){
            activity.runOnUiThread {
                activity.emailLayout.error = null
            }
            check++
        }else{
            activity.runOnUiThread {
                activity.emailLayout.error = activity.getText(R.string.invalid_email)
            }
        }
        if(model.validatePassword()){
            activity.runOnUiThread {
                activity.passwordLayout.error = null
            }
            check++
            if(model.validatePasswords()){
                activity.runOnUiThread {
                    activity.passwordLayout.error = null
                    activity.passwordCheckLayout.error = null
                }
                check++
            }
            else{
                activity.runOnUiThread {
                    activity.passwordLayout.error = activity.getString(R.string.passwords_not_same)
                    activity.passwordLayout.errorIconDrawable = null
                    activity.password.setError(null,null)
                    activity.passwordCheckLayout.error = activity.getString(R.string.passwords_not_same)
                    activity.passwordCheckLayout.errorIconDrawable = null
                    activity.password.setError(null,null)
                }

            }
        }else{
            activity.runOnUiThread {
                activity.passwordLayout.error = activity.getString(R.string.invalid_password)
                activity.passwordLayout.errorIconDrawable = null
                activity.password.setError(null,null)
            }
        }
        if(check == 4){
            model.username.trim()
            model.password.trim()
            model.email.trim()
            makeRegistration()
        }
    }

    private fun makeRegistration(){
        /**
         * Method to make registration api call
         */
        var json = JSONObject()
        json.put("username",model.username)
        json.put("passwd",model.password)
        json.put("email",model.email)
        json.put("bt_mac","..")
        Log.i(TAG,json.toString())

        var api = APICalls()
        var res = api.registration(json)
        if(res.second == 200){
            activity.runOnUiThread {
                Toast.makeText(activity.application, activity.getString(R.string.success_register), Toast.LENGTH_LONG).show()
                activity.goToLogin()
            }
        }
        else if(res.second == 409){
            activity.runOnUiThread {
                activity.usernameLayout.error = activity.getString(R.string.username_exists)
            }
        }
        else if(res.second == 0){
            activity.runOnUiThread {
                Toast.makeText(activity.application, activity.getString(R.string.cant_reach_server), Toast.LENGTH_SHORT).show()
                activity.usernameLayout.error = null
            }
        }
        else{
            activity.runOnUiThread {
                Toast.makeText(activity.application, activity.getString(R.string.error), Toast.LENGTH_SHORT).show()
                activity.usernameLayout.error = null
            }
        }
    }
}
package com.example.proxtalk.userSettings.settingsItems

import com.example.proxtalk.User
import com.example.proxtalk.network.APICalls
import com.example.proxtalk.registration.RegisterModel
import com.lukas.proxtalk.R

class ChangeUsernamePasswordController(
    var activity: ChangeUsernamePasswordActivity
) {
    lateinit var model: RegisterModel
    init {
        model = ChangeUsernamePasswordModel()
    }

    fun saveChanges(){
        /**
         * Method to check if form is filled
         * If username only filled, it will make api call only to change_username, same with password change
         * If both are filled, it will make two api calls
         */
        model.username = activity.binding.newUsername.text.toString().trim()
        model.password = activity.binding.newPassword.text.toString().trim()
        model.password_check = activity.binding.newPasswordCheck.text.toString().trim()
        var actions = 0
        var okActions = 0
        var api = APICalls()
        if(model.username != ""){
            actions++
            if(model.validateUsername()){
                activity.runOnUiThread {
                    activity.binding.newUsernameLayout.error = null
                }
                var res = api.changeUsername(model.username)
                if(res == 200){
                    okActions++
                    User.username = model.username
                    User.saveUsername(activity.application)
                }
                else if(res == 409){
                    activity.runOnUiThread {
                        activity.binding.newUsernameLayout.error = activity.getString(R.string.username_exists)
                    }
                }
                else{
                    activity.runOnUiThread {
                        activity.binding.newUsernameLayout.error = activity.getString(R.string.cant_reach_server)
                    }
                }
            }
            else{
                activity.runOnUiThread {
                    activity.binding.newUsernameLayout.error = activity.getString(R.string.invalid_username)
                }
            }
        }
        if(model.password != ""){
            actions++
            if(model.validatePassword()){
                if(model.validatePasswords()){
                    activity.runOnUiThread {
                        activity.binding.newPasswordLayout.error = null
                        activity.binding.newPasswordLayoutCheck.error = null
                    }
                    var res2 = api.changePassword(model.password,activity.application)
                    if(res2 == 200){
                        okActions++
                    }
                    else{
                        activity.runOnUiThread {
                            activity.binding.newUsernameLayout.error = activity.getString(R.string.cant_reach_server)
                        }
                    }
                }
                else{
                    activity.runOnUiThread {
                        activity.binding.newPasswordLayout.error = activity.getString(R.string.passwords_not_same)
                        activity.binding.newPasswordLayout.errorIconDrawable = null
                        activity.binding.newPassword.setError(null,null)

                        activity.binding.newPasswordLayoutCheck.error = activity.getString(R.string.passwords_not_same)
                        activity.binding.newPasswordLayoutCheck.errorIconDrawable = null
                        activity.binding.newPasswordCheck.setError(null,null)
                    }
                }
            }
            else{
                activity.runOnUiThread {
                    activity.binding.newPasswordLayout.error = activity.getString(R.string.invalid_password)
                    activity.binding.newPasswordLayout.errorIconDrawable = null
                    activity.binding.newPassword.setError(null,null)
                    activity.binding.newPasswordLayoutCheck.error = null
                }
            }
        }
        if(actions == okActions && actions != 0){
            activity.runOnUiThread {
                activity.goBack()
            }
        }
    }
}
package com.example.proxtalk.login

class LoginModel(
    var controller: LoginController,
    var username: String = "",
    var password: String = "",
) {

    fun validatePassword(): Boolean {
        if(password.isEmpty()){
            return false
        }
        return true
    }
    fun validateUsername(): Boolean{
        if(username == ""){
            return  false
        }
        return true
    }


}
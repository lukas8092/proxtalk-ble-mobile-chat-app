package com.example.proxtalk.registration

open class RegisterModel(
    var controller: RegisterController?,
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var password_check: String = ""
) {
    fun validatePassword(): Boolean{
        val match = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&.,-])[A-Za-z\\d@\$!%*#?&.,-]{8,32}\$").find(password)
        return  match != null
    }

    fun validatePasswords(): Boolean{
        return  validatePassword() && password == password_check
    }

    fun validateUsername(): Boolean{
        return username.length in 3..20
    }

    fun validateEmail(): Boolean{
        val match = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").find(email)
        return match != null
    }
}
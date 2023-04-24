package com.example.proxtalk.registration

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.proxtalk.Animations
import com.example.proxtalk.login.LoginActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.lukas.proxtalk.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var controller: RegisterController
    lateinit var button: AppCompatButton
    lateinit var usernameLayout: TextInputLayout
    lateinit var username: TextInputEditText
    lateinit var emailLayout: TextInputLayout
    lateinit var email: TextInputEditText
    lateinit var passwordLayout: TextInputLayout
    lateinit var password: TextInputEditText
    lateinit var passwordCheckLayout: TextInputLayout
    lateinit var passwordCheck: TextInputEditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        controller = RegisterController(this)

        toolbar = findViewById<Toolbar>(R.id.toolbarRegister)
        button = findViewById<AppCompatButton>(R.id.register_btn)
        usernameLayout = findViewById<TextInputLayout>(R.id.reg_username)
        username = findViewById<TextInputEditText>(R.id.reg_username_edit)
        emailLayout = findViewById<TextInputLayout>(R.id.reg_email)
        email = findViewById<TextInputEditText>(R.id.reg_email_edit)
        passwordLayout = findViewById<TextInputLayout>(R.id.reg_password)
        password = findViewById<TextInputEditText>(R.id.reg_password_edit)
        passwordCheckLayout = findViewById<TextInputLayout>(R.id.reg_password_check)
        passwordCheck = findViewById<TextInputEditText>(R.id.reg_password_check_edit)
        toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
        button.setOnClickListener {
            Animations.btnClickAnimation(button)
            button.isActivated = false
            lifecycleScope.launch(Dispatchers.IO){
                controller.registerClick()
                runOnUiThread{
                    button.isActivated = true
                }
            }
        }
    }

    fun goToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
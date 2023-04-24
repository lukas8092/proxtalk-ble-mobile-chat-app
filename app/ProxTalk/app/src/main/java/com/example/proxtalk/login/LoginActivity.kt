package com.example.proxtalk.login

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.proxtalk.Animations
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.User
import com.example.proxtalk.mainActivity.Device
import com.example.proxtalk.mainActivity.MainActivity
import com.example.proxtalk.network.APICalls
import com.example.proxtalk.registration.RegisterActivity
import com.google.android.material.textfield.TextInputLayout
import com.lukas.proxtalk.R
import com.lukas.proxtalk.databinding.ActivityChangeUsernamePasswordBinding
import com.lukas.proxtalk.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    lateinit var controller: LoginController
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())

        controller = LoginController(this)

        if (stateInit()){
            return
        }

        binding.loginBtn.setOnClickListener {
            Animations.btnClickAnimation(binding.loginBtn)
            binding.loginBtn.isEnabled = false
            lifecycleScope.launch(Dispatchers.IO){
                if(controller.loginPressed(binding.username.text.toString().trim(),binding.password.text.toString().trim())){
                    goToMain()
                }
            }
            runOnUiThread {
                binding.loginBtn.isEnabled = true
            }
        }
        Log.i(TAG,"GUUEST:"+ User.readFromStorage("guest-username",application).toString())
        if(User.readFromStorage("guest-username",application) == null && controller.loginInit()){
            goToMain()
        }

        var registerBtn = findViewById<Button>(R.id.reg_btn)
        registerBtn.setOnClickListener {
            Animations.btnClickAnimation(registerBtn)
            goToRegister()
        }
        var guestBtn = findViewById<Button>(R.id.guest_btn)
        guestBtn.setOnClickListener {
            Animations.btnClickAnimation(guestBtn)
            lifecycleScope.launch(Dispatchers.IO){
                guestClicked()
            }

        }

    }

    private fun goToMain(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun guestClicked(){
        var guest = User.continueAsGuest(application)
        Log.i(TAG,guest.first.toString()+ " "+ guest.second.toString())
        if(guest.first != null){
            lifecycleScope.launch(Dispatchers.IO){
                if(controller.loginPressed(guest.first!!, guest.second!!,true)){
                    runOnUiThread {
                        goToMain()
                    }

                }
                else{
                    runOnUiThread {
                        binding.logUsername.error = "Guest creation error"
                    }

                }
            }
        }
    }

    fun stateInit(): Boolean {
        var api = APICalls()
        var res = api.getAppState()
        if(res.first == 200){
            var data = res.second
            Log.i(TAG, res.second.toString())
            if(!(data!!["state"] as Boolean)){
                runOnUiThread {
                    binding.loginLayout.visibility = View.GONE
                    binding.regBtn.visibility = View.GONE
                    binding.infoTitle.visibility = View.VISIBLE
                    binding.infoBody.visibility = View.VISIBLE
                    binding.infoTitle.text = data["title"].toString()
                    binding.infoBody.text = data["body"].toString()
                }
                return true
            }
        }
        return false
    }

}
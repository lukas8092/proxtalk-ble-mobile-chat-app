package com.example.proxtalk.userSettings.settingsItems

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.proxtalk.Animations
import com.lukas.proxtalk.databinding.ActivityChangeUsernamePasswordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangeUsernamePasswordActivity : AppCompatActivity() {
    lateinit var controller: ChangeUsernamePasswordController
    lateinit var binding: ActivityChangeUsernamePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeUsernamePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        controller = ChangeUsernamePasswordController(this)
        binding.toolbarChange.setNavigationOnClickListener {
            super.onBackPressed()
        }
        binding.updateBtn.setOnClickListener {
            Animations.btnClickAnimation(binding.updateBtn)
            binding.updateBtn.isActivated = false
            lifecycleScope.launch(Dispatchers.IO){
                controller.saveChanges()
                runOnUiThread {
                    binding.updateBtn.isActivated = true
                }
            }
        }
    }

    fun goBack(){
        super.onBackPressed()
    }
}
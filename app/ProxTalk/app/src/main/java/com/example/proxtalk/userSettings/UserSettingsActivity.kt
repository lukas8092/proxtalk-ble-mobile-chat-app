package com.example.proxtalk.userSettings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import com.example.proxtalk.User
import com.example.proxtalk.gettingStarted.GettingStartedActivity
import com.example.proxtalk.login.LoginActivity
import com.example.proxtalk.userSettings.settingsItems.ChangeUsernamePasswordActivity
import com.example.proxtalk.userSettings.settingsItems.HistoryActivity
import com.example.proxtalk.userSettings.settingsItems.SettingsProfileImageActivity
import com.lukas.proxtalk.R

class UserSettingsActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var image: ImageView
    lateinit var username: TextView
    lateinit var receivedCount: TextView
    lateinit var sendedCount: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)

        toolbar = findViewById<Toolbar>(R.id.toolbarProfile)
        image = findViewById<ImageView>(R.id.user_profile_image)
        username = findViewById<TextView>(R.id.settings_username)
        username.text = User.username
        receivedCount = findViewById<TextView>(R.id.receivedCount)
        sendedCount = findViewById<TextView>(R.id.sendedCount)

        receivedCount.text = User.recivedStat.toString()
        sendedCount.text = User.sendedStat.toString()
        var profile = initSettingsItem("Profile picture",findViewById(R.id.profile_item),R.drawable.settings_image)
        var username = initSettingsItem("Change username",findViewById(R.id.username_item),R.drawable.user_account_icon)
        var logout = initSettingsItem("Logout",findViewById(R.id.logout_item),R.drawable.logout_icon)
        var history = initSettingsItem("History",findViewById(R.id.history_item),R.drawable.history_icon)
        var guide = initSettingsItem("Getting started",findViewById(R.id.getting_started_item),R.drawable.getting_started_icon)
        toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
        profile.setOnClickListener {
            val intent = Intent(this, SettingsProfileImageActivity::class.java)
            startActivity(intent)
            }
        logout.setOnClickListener {
            logout()
        }
        history.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
        username.setOnClickListener {
            val intent = Intent(this, ChangeUsernamePasswordActivity::class.java)
            startActivity(intent)
        }
        guide.setOnClickListener {
            val intent = Intent(this, GettingStartedActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initSettingsItem(title: String, element: View, idOfIcon: Int): View {
        /**
         * Method to init settings item
         */
        element.findViewById<TextView>(R.id.settings_item_title).text = title
        element.findViewById<ImageView>(R.id.settings_item_icon).setImageResource(idOfIcon)
        return element
    }

    private fun logout(){
        /**
         * Method to delete user data from local storage, and open login activity without back option
         */
        User.logout(application)
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if(User.profileImageDrawable != null){
            image.setImageDrawable(User.profileImageDrawable)
        }
        username.text = User.username
    }

}
package com.example.proxtalk.gettingStarted

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import com.lukas.proxtalk.R

class GettingStartedActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getting_started)
        toolbar = findViewById<Toolbar>(R.id.toolbarGettingStarted)
        toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
    }
}
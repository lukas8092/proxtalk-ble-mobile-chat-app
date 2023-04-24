package com.example.proxtalk.userSettings.settingsItems

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.User
import com.example.proxtalk.network.APICalls
import com.example.proxtalk.mainActivity.MessageItem
import com.example.proxtalk.mainActivity.MessagesAdapter
import com.lukas.proxtalk.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class HistoryActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var messagesView: RecyclerView
    lateinit var messagesAdapter: MessagesAdapter
    lateinit var refreshView: SwipeRefreshLayout
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        toolbar = findViewById<Toolbar>(R.id.toolbarHistory)
        messagesView = findViewById<RecyclerView>(R.id.messageHistoryList)
        refreshView = findViewById<SwipeRefreshLayout>(R.id.refreshView)
        messagesAdapter = MessagesAdapter()
        messagesView.layoutManager = LinearLayoutManager(this)
        messagesView.adapter = messagesAdapter
        refreshView.isRefreshing = true
        toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            loadHistory()
        }
        refreshView.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO) {
                loadHistory()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadHistory(){
        /**
         * Method to load history of messages into message adapter from api call
         */
        var api = APICalls()
        var res = api.getHistory()
        if(res.second == 200){
            var messages = MessagesAdapter()
            var array = JSONArray(res.first)
            for(i in 0 until array.length()){
                try{
                    var msg = MessageItem("",0)
                    var item = array.get(i)
                    var data = JSONObject(item.toString())
                    msg.id = data["id"].toString().toInt()
                    msg.messageText = data["content"].toString()
                    msg.username = User.username
                    var reactions = JSONObject(data["reactions"].toString())
                    msg.reactionCount = reactions["1"].toString().toInt()
                    msg.image = data["image"] is String
                    messages.addMessage(msg)
                }catch(e:java.lang.Exception){
                    Log.i(TAG,e.toString())
                    continue
                }
            }
            runOnUiThread {
                messagesView.adapter = messages
            }
            refreshView.isRefreshing = false
        }
    }
}
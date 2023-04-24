package com.example.proxtalk.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.Codes
import com.example.proxtalk.User
import com.example.proxtalk.mainActivity.Device
import com.lukas.proxtalk.R


object Notifications {
    var messageRecivedNotification: Boolean = false
    fun createNotificationChannel(): NotificationChannel? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "state"
            val descriptionText = "state of app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Codes.NOTIFICATIONS_CHANNEL_ID.code.toString(), name, importance).apply {
                description = descriptionText
            }
           return channel
        }
        return null
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(title: String, content: String,id: Int): Notification {
        /**
         * Method to create notification object
         */
        var builder = NotificationCompat.Builder(User.act.application, Codes.NOTIFICATIONS_CHANNEL_ID.code.toString())
            .setSmallIcon(R.drawable.notification_scan_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        return builder.build()
    }

    @SuppressLint("MissingPermission")
    fun sendBackgroundNotification() {
        /**
         * Method to send notification that app is running on background
         */
        with(NotificationManagerCompat.from(User.act.application)) {
            var n = sendNotification("ProxTalk", "App scanning on background", 1)
            n.flags = Notification.FLAG_NO_CLEAR
            notify(1, n)
        }
    }

    @SuppressLint("MissingPermission")
    fun sendNotificationReceived(){
        /**
         * Method to send notifications when new message arrive and app is on background
         */
        Log.i(TAG,messageRecivedNotification.toString() + " "+ Device.isOnBackground)
        if(!messageRecivedNotification && Device.isOnBackground){
            with(NotificationManagerCompat.from(User.act.application)) {
                var n = sendNotification("ProxTalk", "New message was found", 2)
                notify(2, n)
            }
            messageRecivedNotification = true
        }

    }




}
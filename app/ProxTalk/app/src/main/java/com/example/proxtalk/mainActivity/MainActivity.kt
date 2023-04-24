package com.example.proxtalk.mainActivity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proxtalk.BLE.BLEClient
import com.example.proxtalk.BLE.UUIDS
import com.example.proxtalk.BLEServer
import com.example.proxtalk.Codes
import com.example.proxtalk.Permissions
import com.example.proxtalk.User
import com.example.proxtalk.createMessage.CreateMessageActivity
import com.example.proxtalk.gettingStarted.GettingStartedActivity
import com.example.proxtalk.login.LoginActivity
import com.example.proxtalk.network.Ws
import com.example.proxtalk.notifications.Notifications
import com.example.proxtalk.userSettings.UserSettingsActivity
import com.google.android.material.appbar.MaterialToolbar
import com.lukas.proxtalk.R
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var controller:MainActivityController
    lateinit var btAdapter: BluetoothAdapter
    lateinit var messagesAdapter: MessagesAdapter
    lateinit var bleServer: BLEServer
    lateinit var messagesView: RecyclerView
    lateinit var toolbar: MaterialToolbar
    lateinit var permissionStack: ArrayDeque<Pair<String,Int>>
    lateinit var scanIndicator: TextView
    lateinit var serverIndicator: TextView
    var scanner: BluetoothLeScanner? = null
    var ws: Ws? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Other
        controller = MainActivityController(this)
        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build())

        User.act = this
        User.getStat()

        initGettingStarted()

        //Indicators
        scanIndicator = findViewById<TextView>(R.id.scanIndicator)
        serverIndicator = findViewById<TextView>(R.id.serverIndicator)

        //Bt staff
        btAdapter = BluetoothAdapter.getDefaultAdapter()
        bleServer = BLEServer()

        // Recycler view init
        messagesAdapter = MessagesAdapter()
        messagesView = findViewById<RecyclerView>(R.id.recyclerView)
        messagesView.layoutManager = LinearLayoutManager(this)
        messagesView.adapter = messagesAdapter
        messagesAdapter.addMessage(MessageItem("App started"))

        // Toolbar
        toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.toolbar_menu)
        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.send_page -> goToCreateMessageActivity()
                R.id.user_settings -> goToUserSettings()
                R.id.random_message -> controller.getRandomMessage()
            }
            true
        }

        // Permissions
        permissionStack = ArrayDeque(Permissions.listOfPermissons)

        // Notifications
        initNotifications()

        // Profile Image
        User.loadProfilePicture()

        //Websocket
        Device.openWebsocket()
    }



    fun makeToast(text:String){
        runOnUiThread {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
        }
    }


    // Activity lifecycle
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        permissionStack = ArrayDeque(Permissions.listOfPermissons)
        Permissions.runPermissionStack()
    }

    override fun onPause() {
        super.onPause()
        Device.isOnBackground = true
        Notifications.sendBackgroundNotification()
    }

    override fun onResume() {
        super.onResume()
        cancelNotification(1)
        cancelNotification(2)
        Device.isOnBackground = false
        Notifications.messageRecivedNotification = false
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelNotification(1)
        cancelNotification(2)
        Notifications.messageRecivedNotification = false
    }

    // Notifications
    @RequiresApi(Build.VERSION_CODES.O)
    fun initNotifications(){
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Notifications.createNotificationChannel()
            ?.let { notificationManager.createNotificationChannel(it) }
    }
    private fun cancelNotification(id: Int){
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.cancel(id)
    }
    private fun goToCreateMessageActivity(){
        val intent = Intent(this, CreateMessageActivity::class.java)
        startActivity(intent)
    }
    private fun goToUserSettings(){
        val intent = Intent(this, UserSettingsActivity::class.java)
        startActivity(intent)
    }
    fun goToLogin(){
        runOnUiThread{
            Toast.makeText(this@MainActivity, getString(R.string.session_expired), Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    // Permissions
    @RequiresApi(Build.VERSION_CODES.O)
    fun requestLocation() {
        var state = getLocationState()
        if(!state){
            startActivityForResult(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                Codes.LOCATION_ENABLE_CODE.code)
        }else{
            Permissions.locationState = true
            Permissions.runPermissionStack()
        }

    }
    fun getLocationState(): Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun turnOnBluetooth(){
        if(btAdapter.isEnabled){
            Permissions.bluetoothState = true
            Permissions.runPermissionStack()
        }else{
            Permissions.bluetoothState = false
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, Codes.BLUETOOTH_ENABLE_CODE.code)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Permissions.onActivityResult(requestCode, resultCode, data)
    }

    // BLE
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    fun initScanBLE(){
        /**
         * Method to init BLE scanning
         * It will set scan settings and filter, than start scanning
         */
        if(scanner != null){
            return
        }
        scanner = btAdapter.bluetoothLeScanner
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()

        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(UUIDS.SERVICE_UUID)).build()

        if (!btAdapter.isMultipleAdvertisementSupported) {
            messagesAdapter.addMessage(MessageItem(getString(R.string.ble_not_supported)))
            return
        }
        var scan = scanner
        scan!!.startScan(listOf(scanFilter), settings, BLEClient.mScanCallback)
        Log.i(UUIDS.TAG,"Scan started")
    }

    fun initGettingStarted(){
        if(User.readFromStorage("i",application) == null){
            User.saveToStorage("i","",application)
            val intent = Intent(this, GettingStartedActivity::class.java)
            startActivity(intent)
        }
    }


}
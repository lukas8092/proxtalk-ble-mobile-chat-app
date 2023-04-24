package com.example.proxtalk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proxtalk.BLE.BtState

object Permissions {
    var bluetoothState = false
    var locationState = false
    var readStorageState = false
    var requesting: BtState = BtState.SCAN
    @RequiresApi(Build.VERSION_CODES.S)
    var listOfPermissons = listOf(
        Pair(Manifest.permission.ACCESS_FINE_LOCATION,Codes.LOCATION_FINE_CODE.code),
        Pair(Manifest.permission.ACCESS_FINE_LOCATION,Codes.LOCATION_COARSE_CODE.code),
        Pair(Manifest.permission.BLUETOOTH_ADVERTISE,0),
        Pair(Manifest.permission.BLUETOOTH_CONNECT,0),
        Pair(Manifest.permission.BLUETOOTH_SCAN,0),
        Pair("",Codes.BLUETOOTH_ENABLE_CODE.code),
        Pair("",Codes.LOCATION_ENABLE_CODE.code))

    @RequiresApi(Build.VERSION_CODES.O)
    fun runPermissionStack(){
        /**
         * Method to prompt for every required permission in permission stack
         * After emptying stack it will start BLE services
         */
        if(!User.act.permissionStack.isEmpty()){
            var item = User.act.permissionStack.pop()
            if(item.second == Codes.BLUETOOTH_ENABLE_CODE.code){
                User.act.turnOnBluetooth()
            }
            else if(item.second == Codes.LOCATION_ENABLE_CODE.code){
                User.act.requestLocation()
            }
            else{
                User.act.requestPermissions(
                    arrayOf(item.first),
                    item.second)
            }
        }
        else{
            runServices()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun runServices(){
        /**
         * Method to run BLE services depending on actual needed actions
         */
        if(requesting == BtState.SCAN && locationState && bluetoothState){
            User.act.initScanBLE()
        }
        if(requesting == BtState.BOTH && locationState && bluetoothState){
            User.act.initScanBLE()
            User.act.bleServer.start(User.act.application)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        /**
         * Callback from permissions activities
         */
        val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        if(requestCode == Codes.EXTERNAL_STORAGE_READ_CODE.code){
            readStorageState = granted
        }
        if(requestCode == Codes.LOCATION_COARSE_CODE.code || requestCode == Codes.LOCATION_FINE_CODE.code){
            locationState = granted
        }
        runPermissionStack()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /**
         * Callback from permissions activities
         */
        if(requestCode == Codes.LOCATION_ENABLE_CODE.code){
            locationState = User.act.getLocationState()
        }
        if(requestCode == Codes.BLUETOOTH_ENABLE_CODE.code){
            Permissions.bluetoothState = resultCode == -1
        }
        runPermissionStack()
    }
}
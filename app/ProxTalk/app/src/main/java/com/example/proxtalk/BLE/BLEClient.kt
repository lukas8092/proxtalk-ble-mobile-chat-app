package com.example.proxtalk.BLE

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.proxtalk.Animations
import com.example.proxtalk.User
import com.example.proxtalk.mainActivity.Device
import java.time.LocalDateTime

object BLEClient {

    val mScanCallback: ScanCallback = object : ScanCallback() {
        /**
         * Callback called when BLE scanning finds BLE device
         */
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            /**
             * Method where device will be processed
             * It will try to connect to that device and add to a list
             * which stands for time before connecting to same device again
             */
            val device: BluetoothDevice = result!!.device
            Log.i(UUIDS.TAG,"Found BLE device:"+ device.address)
            Animations.scanBlink()
            var connect = false
            var now = LocalDateTime.now()
            var msg = Device.devicesMessages[device.address]
            if (msg != null) {
                if(now >= msg.time.plusSeconds(Device.scanDeviceDelay)){
                    connect = true
                }
            }
            else{
                connect = true
            }
            if(connect){
                device.connectGatt(User.act.application, false, GattClientCallback())
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.i(UUIDS.TAG, "Scan error:$errorCode")
        }
    }

    class GattClientCallback : BluetoothGattCallback() {
        /**
         * Callback when device successfully connected
         */
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            /**
             * When device is connected it will discover all available services what triggers another function
             */
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(UUIDS.TAG, "Connected to BLE device:"+ gatt.device.name + " "+ gatt.device.address)
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.close()
                Log.i(UUIDS.TAG, "Disconnected from BLE device:"+ gatt.device.name + " "+ gatt.device.address)
            }
        }

        @SuppressLint("MissingPermission")
        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            /**
             * Method which will be call when server requesting MTU, which is for requesting more bytes to be received
             * In normal BLE you can only transfer 20 bytes, with MTU up to 512 bytes
             * It will check if it contains required service and characteristic
             * and get and read the characteristic which will trigger another function
             */
            super.onMtuChanged(gatt, mtu, status)
            try {
                var a = gatt?.getService(UUIDS.SERVICE_UUID)
                if (a != null) {
                    Log.i(UUIDS.TAG,a.uuid.toString())
                    var char = a.getCharacteristic(UUIDS.CHARACTERISTIC_UUID)
                    gatt?.readCharacteristic(char)
                }
            }
            catch (e:java.lang.Exception){
                Log.i(UUIDS.TAG, "Characteristic error:$e")
            }
        }
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            /**
             * Method which will be called when service is found
             * And there will be MTU request for 512 bytes
             */
            gatt?.requestMtu(512)
        }
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("MissingPermission")
        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            /**
             * Method that's trigger when characteristic is read
             * It will convert incoming bytes into string and handle it to main activity to verify it
             */
            if (UUIDS.CHARACTERISTIC_UUID == characteristic!!.uuid) {
                val data = characteristic.value
                val value = String(data)
                Log.i(UUIDS.TAG,"Received data from "+ gatt!!.device.name+ " "+ gatt.device.address+":\n"+ value )
                User.act.controller.addMessage(value,gatt.device.address)
                gatt?.close()
            }
        }

    }
}
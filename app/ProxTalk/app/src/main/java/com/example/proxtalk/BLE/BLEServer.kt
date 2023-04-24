package com.example.proxtalk

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGattCharacteristic.*
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.example.proxtalk.BLE.UUIDS.CHARACTERISTIC_UUID
import com.example.proxtalk.BLE.UUIDS.SERVICE_UUID
import com.example.proxtalk.BLE.UUIDS.TAG
import java.util.*

class BLEServer {

    lateinit var btManager: BluetoothManager
    lateinit var btAdvertiser: BluetoothLeAdvertiser
    lateinit var btServer: BluetoothGattServer

    @SuppressLint("MissingPermission")
    fun start(app:Application) {
        /**
         * Method to start BLE server, that will advertise data
         */
        if(User.message == null || this::btServer.isInitialized){
            return
        }
        btManager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter = btManager.adapter
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTimeout(0)
            .build()
        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addServiceUuid(ParcelUuid(SERVICE_UUID))
            .build()
        btServer = btManager.openGattServer(app, gattServerCallback);
        btServer.addService(createService())
        btAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        btAdvertiser.startAdvertising(settings, data, mAdvertiseCallback)
        Log.i(TAG,"BLE Server started")
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        /**
         * Callback that its called when somebody read characteristic
         * It checks if its the right characteristic defined in UUIDS class
         * If yes, it will send response with actual message in given protocol
         */
        @SuppressLint("MissingPermission")
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int, offset: Int, characteristic: BluetoothGattCharacteristic
        ) {
            Animations.serverBlink()
            if (CHARACTERISTIC_UUID == characteristic.uuid) {
                if(User.message != null){
                    var text: String = User.message!!.createString()
                    Log.i(TAG,"Sending data to "+ device!!.name+ " "+ device.address+ ":\n"+ text)
                    val value: ByteArray = text.toByteArray()
                    btServer.sendResponse(device, requestId, GATT_SUCCESS, 0, value)
                }
            }
        }
    }

    private val mAdvertiseCallback: AdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.i(TAG, "BLE Advertise Started")
        }
        override fun onStartFailure(errorCode: Int) {
            Log.w(TAG, "BLE Advertise Failed: $errorCode")
        }
    }

    private fun createService(): BluetoothGattService? {
        /**
         * Method that will create service for advertising
         * It creates service defined in UUIDS class and put into it one characteristic
         * @return service object
         */
        val service = BluetoothGattService(SERVICE_UUID, SERVICE_TYPE_PRIMARY)
        val char = BluetoothGattCharacteristic(CHARACTERISTIC_UUID,PROPERTY_READ, PERMISSION_READ)
        service.addCharacteristic(char)
        return service
    }
}
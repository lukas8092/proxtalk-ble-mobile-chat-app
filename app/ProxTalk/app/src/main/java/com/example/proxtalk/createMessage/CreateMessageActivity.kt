package com.example.proxtalk.createMessage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.proxtalk.*
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.lib.URIPathHelper
import com.example.proxtalk.mainActivity.Device
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.lukas.proxtalk.R
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class CreateMessageActivity : AppCompatActivity() {
    lateinit var controller: CreateMessageController

    lateinit var toolbar: Toolbar
    lateinit var messageText: TextInputEditText
    lateinit var messageTextLayout: TextInputLayout
    lateinit var sendButton: AppCompatButton
    lateinit var addImageButton: AppCompatButton
    lateinit var selectedImageText: TextView
    lateinit var previewImage: ImageView
    lateinit var progressBar: ProgressBar
    var imageFile: File? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         * Initializing elements, setting listeners
         */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_message)
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build())
        controller = CreateMessageController(this)

        toolbar = findViewById<Toolbar>(R.id.toolbarMessage)
        messageText = findViewById<TextInputEditText>(R.id.messageText)
        messageTextLayout = findViewById<TextInputLayout>(R.id.messageTextlayout)
        sendButton = findViewById<AppCompatButton>(R.id.send)
        addImageButton = findViewById<AppCompatButton>(R.id.addImageBtn)
        selectedImageText = findViewById<TextView>(R.id.selectedImage)
        progressBar = findViewById<ProgressBar>(R.id.sendProgress)
        var actualMessage = findViewById<View>(R.id.actualMessage)
        var messageProfileImg = actualMessage.findViewById<ImageView>(R.id.user_profile_image)
        messageProfileImg.setImageDrawable(User.profileImageDrawable)
        progressBar.isVisible = false
        messageText.requestFocus()
        toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
        sendButton.setOnClickListener {
            Animations.btnClickAnimation(sendButton)
            sendButton.isEnabled = false
            progressBar.isVisible = true
            lifecycleScope.launch(Dispatchers.IO){
                controller.sendMessage()
                runOnUiThread {
                    sendButton.isEnabled = true
                    progressBar.isVisible = false
                }
            }
        }
        var msgUsername = actualMessage.findViewById<TextView>(R.id.msg_username)
        msgUsername.text = User.username
        previewImage = actualMessage.findViewById<ImageView>(R.id.imageViewMsg)
        previewImage.isVisible = false
        var msgText = actualMessage.findViewById<TextView>(R.id.msgText)
        msgText.text = ""
        messageText.addTextChangedListener {
            msgText.text = messageText.text
        }
        addImageButton.setOnClickListener {
            Animations.btnClickAnimation(addImageButton)
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Codes.EXTERNAL_STORAGE_READ_CODE.code)
        }

    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         * Method called after image picker activity is completed
         * It will save picked image file object and compress it
         */
        try{
        if (resultCode == RESULT_OK && requestCode == 105) {

                var uri = data?.data
                if (uri != null) {
                    Log.i(TAG,uri.path.toString())
                    var imageFileTmp: File? = null
                    try{
                        imageFileTmp = File(URIPathHelper.getPath(application,uri))
                    }catch(e:java.lang.Exception){
                        Toast.makeText(application, getString(R.string.image_error), Toast.LENGTH_LONG).show()
                        return
                    }
                    Log.i(TAG,"Name:"+ imageFileTmp.name)
                    Log.i(TAG,"Ext:"+ imageFileTmp.extension)
                    Log.i(TAG,"Size:"+ imageFileTmp.length())
                    if(((imageFileTmp.length()/1024)/1024) >= 10){
                        Toast.makeText(application, getString(R.string.image_is_too_big), Toast.LENGTH_LONG).show()
                        return
                    }
                    if(Device.allowedImageExtension.contains(imageFileTmp.extension.lowercase())){

                        lifecycleScope.launch(Dispatchers.IO){
                            runOnUiThread {
                                previewImage.setImageURI(uri)
                                selectedImageText.text = imageFileTmp.name
                                previewImage.isVisible = true
                           }
                            val compressedImageFile = Compressor.compress(application, imageFileTmp){
                                quality(30)
                            }
                            Log.i(TAG,"Compressed size:"+ compressedImageFile.length())
                            imageFile = compressedImageFile
                        }

                    }else{
                        Toast.makeText(application, getString(R.string.invalid_image_extension), Toast.LENGTH_LONG).show()
                    }
            }
        }
        }catch(e:java.lang.Exception){
            Log.i(TAG,e.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        /**
         * Callback from requesting STORAGE permissions
         */
        val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        if(requestCode == Codes.EXTERNAL_STORAGE_READ_CODE.code){
            if(granted){
                openImageSelect()
                Permissions.readStorageState = true
            }else{
                Toast.makeText(application, getString(R.string.storage_not_granted), Toast.LENGTH_LONG).show()
                Permissions.readStorageState = false
            }
        }
        Permissions.runPermissionStack()
    }

    private fun openImageSelect(){
        /**
         * Creating intent to open user image picker
         */
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.type = "image/*"
        startActivityForResult(gallery, 105)
    }

    fun goToMainActivity(){
        super.onBackPressed();
    }
}
package com.example.proxtalk.userSettings.settingsItems

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.proxtalk.Animations
import com.example.proxtalk.Codes
import com.example.proxtalk.Permissions
import com.example.proxtalk.User
import com.example.proxtalk.network.APICalls
import com.example.proxtalk.lib.URIPathHelper
import com.example.proxtalk.mainActivity.Device
import com.lukas.proxtalk.R
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SettingsProfileImageActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    var newImageFile: File? = null
    lateinit var changedImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_profile_image)
        toolbar = findViewById<Toolbar>(R.id.toolbarProfileImage)
        changedImage = findViewById<ImageView>(R.id.changedImage)
        changedImage.setImageDrawable(User.profileImageDrawable)
        var newImageBtn = findViewById<Button>(R.id.newImage)
        toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
        newImageBtn.setOnClickListener {
            Animations.btnClickAnimation(newImageBtn)
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Codes.EXTERNAL_STORAGE_READ_CODE.code)
        }

        var upload = findViewById<Button>(R.id.uploadNewImage)
        upload.setOnClickListener {
            Animations.btnClickAnimation(upload)
            upload.isEnabled = false
            lifecycleScope.launch(Dispatchers.IO){
                if(newImageFile != null){
                    var api = APICalls()
                    var res = api.postNewProfileImage(application, newImageFile!!)
                    if(res == 200){
                        User.profileImageDrawable = changedImage.drawable
                        runOnUiThread {
                            super.onBackPressed()
                        }
                    }
                }
                runOnUiThread {
                    upload.isEnabled = true
                }
            }

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
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.type = "image/*"
        startActivityForResult(gallery, 105)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /**
         * Call back when image is picked,
         * It will check for allowed extension, compress image and save it
         */
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 105) {
            var uri = data?.data
            if (uri != null) {
                var imageFile = File(URIPathHelper.getPath(application,uri))
                if(((imageFile.length()/1024)/1024) >= 10){
                    Toast.makeText(application, getString(R.string.image_is_too_big), Toast.LENGTH_LONG).show()
                    return
                }
                if(Device.allowedImageExtension.contains(imageFile.extension.lowercase())){
                    changedImage.setImageURI(uri)
                    lifecycleScope.launch(Dispatchers.IO){
                        val compressedImageFile = Compressor.compress(application, imageFile){
                            quality(40)
                        }
                        newImageFile = compressedImageFile
                    }
                }else{
                    Toast.makeText(application, getString(R.string.invalid_image_extension), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
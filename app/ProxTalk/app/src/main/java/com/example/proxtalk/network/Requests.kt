package com.example.proxtalk.network

import android.app.Application
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.proxtalk.BLE.UUIDS.TAG
import com.example.proxtalk.User
import com.example.proxtalk.network.RequestsBase.urlBase
import com.example.proxtalk.lib.BasicAuthInterceptor
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URL

object RequestsBase{
    val urlBaseWs = "wss://proxtalk.live:85"
    val urlBase = "https://proxtalk.live:81"
}
class Requests {

    fun Get(uri: String,init:Boolean=false,username: String?=null,password:String?=null): Pair<String?, Int?> {
        /**
         * Method to do get request to any host
         * Parameter init can prevent from recursive calling of method checkCodes
         * @return Pair of response and status code
         */
        var client = OkHttpClient.Builder()
        if (username != null && password != null){
            client.addInterceptor(BasicAuthInterceptor(username,password))
        }
        try {
            val url = URL("$urlBase/$uri")
            Log.i(TAG, "GET:$url")
            val request = Request.Builder().url(url).build()
            val response = client.build().newCall(request).execute()
            val result = response.body?.string()
            Log.i(TAG,"GET RESULT:"+ response.code)
            checkCodes(response.code,init)
            return Pair(result,response.code)
        }catch (e:java.lang.Exception){
            return Pair("",0)
        }
    }

    fun PostWithBody(uri: String, body: String): Pair<String?, Int?> {
        /**
         * Method to do any post request with json body as a string
         * @return Pair of response and status code
         */
        var client: OkHttpClient = OkHttpClient()
        try {
            val url = URL("$urlBase/$uri")
            Log.i(TAG, "POST:$url")
            val requestBody = body.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder().url(url).post(requestBody).build()
            val response = client.newCall(request).execute()
            val result = response.body?.string()
            Log.i(TAG,"POST RESULT:"+ response.code)
            checkCodes(response.code)
            return Pair(result,response.code)
        }catch (e:java.lang.Exception){
            return Pair("",0)
        }
    }

    fun PostImage(uri: String,imageFile: File,app:Application,header_params: Array<Pair<String,String>>): Int {
        /**
         * Method to upload image to any endpoint
         * Param header_params - array of headers
         * @return Pair of response and status code
         */
        val mimeType = getMimeType(imageFile)
        try {
            val requestBody: RequestBody =
                MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", imageFile.name,imageFile.asRequestBody(mimeType?.toMediaTypeOrNull()))
                    .build()
            val requestBuild = Request.Builder().url(urlBase+uri)
            for(attr in header_params){
                requestBuild.addHeader(attr.first,attr.second)
            }
            val request: Request = requestBuild.post(requestBody).build()
            var client = OkHttpClient()
            val response: Response = client.newCall(request).execute()
            checkCodes(response.code)
            return response.code
        }catch(e:java.lang.Exception){
            return  0
        }
    }

    private fun getMimeType(file: File): String? {
        /**
         * Method to get media type from object file
         */
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    private fun checkCodes(statusCode: Int, init: Boolean=false){
        /**
         * Method to check if token is valid
         * If is not valid it will open login activity
         */
        if(statusCode == 401 && !init){
            User.act.goToLogin()
        }
    }

}
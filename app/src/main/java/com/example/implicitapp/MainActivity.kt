package com.example.implicitapp

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sendSMS

class MainActivity : AppCompatActivity() {
    val CALL_REQUEST_CODE = 101
//    for image capture
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermisions()
//        making a call
        imgCall.setOnClickListener {
            val phoneNumber = etPhone.getText()
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("Phone: $phoneNumber")
            startActivity(intent)
        }

//        sending a message
        imgMessage.setOnClickListener {
            val phoneNumber = etPhone.getText().toString()
            val message = etMessage.getText().toString()
            sendSMS(phoneNumber, message)
        }
//        browsing the web
        imgWeb.setOnClickListener {
            val url = etWeb.getText().toString()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
//        take a photo
        imgCamera.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
//                    permission was denied
                    val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
//                    permission was already granted
                    openCamera()
                }
            } else {
//                system os is mashmallow
                openCamera()
            }
        }
//        mpesa app
        imgMpesa.setOnClickListener {
            val simToolKitLaunchIntent: Intent? =
                this@MainActivity.getPackageManager().getLaunchIntentForPackage("com.android.stk")
            simToolKitLaunchIntent?.let {startActivity(it)}
        }
//        go to gallery
        imgGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("content://media/external/images/media")
            startActivity(intent)
        }
//        call log
        imgLog.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.type = CallLog.Calls.CONTENT_TYPE
            startActivity(intent)
        }
    }


    private fun setupPermisions() {
        val permission = ContextCompat.checkSelfPermission(this, permission.CALL_PHONE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("None", "Permission to call has been denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
        arrayOf(permission.CALL_PHONE),
        CALL_REQUEST_CODE)
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"New picture")
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//        camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        called when image is captured from the camera intent
        if (resultCode == Activity.RESULT_OK) {
//            set image captured to the image view below
            imgDisplay.setImageURI(image_uri)
        }
    }
}

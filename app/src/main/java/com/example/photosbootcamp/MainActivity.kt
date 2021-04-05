package com.example.photosbootcamp

import android.Manifest.permission.*
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    var image_uri : Uri? = null

    companion object {
        private const val PERMISSION_CODE_PICK_IMAGE = 1000
        private const val IMAGE_PICK_CODE = 1001
        private const val PERMISSION_CODE_ACCESS_CAMERA = 1011
        private const val PERMISSION_CODE_WRITE_EXTERNAL_STORAGE = 1111

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pick_button.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                    val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_PICK_IMAGE)
                }else{
                    pickImageFromGalery()
                }
            }else{
                pickImageFromGalery()
            }
        }
        open_camera_button.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(CAMERA) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        val permission = arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE)
                        requestPermissions(permission, PERMISSION_CODE_ACCESS_CAMERA)
                }else{
                    openCamera()
                }
            }else{
                openCamera()
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Nova foto")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Imagem capturada pela camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, PERMISSION_CODE_ACCESS_CAMERA)

    }

    private fun pickImageFromGalery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image_view.setImageURI(data?.data)
        }
        if(resultCode == Activity.RESULT_OK && requestCode == PERMISSION_CODE_ACCESS_CAMERA){
            image_view.setImageURI(image_uri)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE_PICK_IMAGE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGalery()
                }else{
                    Toast.makeText(this,"Permissão Negada!",Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_CODE_ACCESS_CAMERA -> {
                if(grantResults.size > 1  &&
                   grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                   grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }else{
                    Toast.makeText(this,"Permissão Negada!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
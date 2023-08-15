package com.example.expensetracker.views

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityRecieptTrackingBinding
import java.io.IOException

class ReceiptTrackingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRecieptTrackingBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecieptTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addReceiptbtn.setOnClickListener {

        }
        binding.imageView.setOnClickListener {
            openCameraOrGallery()
        }

    }


    private fun openCameraOrGallery() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> openCamera()
                "Choose from Gallery" -> openGallery()
                "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    if (imageBitmap != null) {
                        binding.imageView.setImageBitmap(imageBitmap)
                    } else {
                        Toast.makeText(this, "Failed to get image from camera", Toast.LENGTH_SHORT).show()
                    }
                }
                100 -> {
                    if (data != null && data.data != null) {
                        val imageUri = data.data
                        try {
                            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                            binding.imageView.setImageBitmap(imageBitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "Action canceled", Toast.LENGTH_SHORT).show()
        }
    }

}
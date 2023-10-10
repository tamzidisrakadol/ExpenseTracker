package com.example.expensetracker.views

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.databinding.ActivityRecieptTrackingBinding
import com.example.expensetracker.model.ReceiptModal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.UUID

class ReceiptTrackingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRecieptTrackingBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var imgUri:Uri
    private var selectImage = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecieptTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addReceiptbtn.setOnClickListener {
            if (selectImage){
                uploadReceipt()
            }else{
                Toast.makeText(this, "Select Receipt first", Toast.LENGTH_SHORT).show()
            }

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
                        imgUri = getImageUri(this, imageBitmap)
                        selectImage = true
                    } else {
                        selectImage = false
                        Toast.makeText(this, "Failed to get image from camera", Toast.LENGTH_SHORT).show()
                    }
                }
                100 -> {
                    if (data != null && data.data != null) {
                        imgUri = data.data!!
                        try {
                            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imgUri)
                            binding.imageView.setImageBitmap(imageBitmap)
                            selectImage = true
                        } catch (e: IOException) {
                            selectImage = false
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "Action canceled", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }


    private fun uploadReceipt(){
        val progressDialog= ProgressDialog(this);
        progressDialog.setTitle("Just a moment")
        progressDialog.setMessage("saving the receipt")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        val receiptId = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("images/$receiptId")
        storageRef.putFile(imgUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {

                saveToFireStore(currentUserID!!,receiptId,it.toString())
            }
            Toast.makeText(this, "upload successfully", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            val intent = Intent(this@ReceiptTrackingActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

        }.addOnFailureListener{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }

    }

    private fun saveToFireStore(uid:String,receiptId:String,imgUrl:String){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Months are zero-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate ="$day/$month/$year"
        val receiptModal = ReceiptModal(uid = uid, receiptId = receiptId, imgUrl = imgUrl, date = currentDate)
        FirebaseFirestore.getInstance().collection("Receipt").add(receiptModal)

    }

}
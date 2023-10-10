package com.example.expensetracker.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityShowReceiptBinding
import com.example.expensetracker.model.ReceiptModal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ShowReceiptActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowReceiptBinding
    private val receiptList = mutableListOf<ReceiptModal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getReceiptList()
    }

    private fun getReceiptList() {
        lifecycleScope.launch {
            FirebaseFirestore.getInstance()
                .collection("Receipt")
                .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                        val receiptModalData= document.toObject(ReceiptModal::class.java)
                        receiptList.add(receiptModalData)
                    }
                    Log.d("list","size of list ${receiptList.size}")
                }
        }

    }
}
package com.example.expensetracker.views

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.adapter.ReceiptAdapter
import com.example.expensetracker.adapter.ReceiptItemClickListner
import com.example.expensetracker.databinding.ActivityShowReceiptBinding
import com.example.expensetracker.model.ReceiptModal
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowReceiptActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowReceiptBinding
    private val receiptList = mutableListOf<ReceiptModal>()
    private lateinit var adapter: ReceiptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getReceiptList()
        binding.receiptList.layoutManager = GridLayoutManager(this,2)
        binding.receiptList.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        binding.receiptList.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL))

    }

    private fun getReceiptList() {

        val progressDialog= ProgressDialog(this);
        progressDialog.setTitle("Just a moment")
        progressDialog.setMessage("showing the receipt")
        progressDialog.setCancelable(false)
        progressDialog.show()

        receiptList.clear()
        lifecycleScope.launch {
            FirebaseFirestore.getInstance()
                .collection("Receipt")
                .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                        val receiptModalData= document.toObject(ReceiptModal::class.java)
                        receiptModalData.receiptId = document.id
                        receiptList.add(receiptModalData)
                    }
                    progressDialog.dismiss()

                     adapter = ReceiptAdapter(receiptList = receiptList,this@ShowReceiptActivity, object:ReceiptItemClickListner{

                        override fun onItemClick(receiptModal: ReceiptModal, pos: Int) {
                            deleteReceipt(receiptModal,pos)

                        }

                    })
                    binding.receiptList.adapter = adapter
                    adapter.notifyDataSetChanged()

                }
        }

    }

    private fun deleteReceipt(receiptModal: ReceiptModal,pos:Int){
        FirebaseFirestore.getInstance()
            .collection("Receipt")
            .document(receiptModal.receiptId)
            .delete()
            .addOnSuccessListener {
                receiptList.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                getReceiptList()
                Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show()

            }
    }



}
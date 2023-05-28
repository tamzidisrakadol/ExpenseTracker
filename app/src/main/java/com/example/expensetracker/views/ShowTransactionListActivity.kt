package com.example.expensetracker.views

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.adapter.OnItemClickListener
import com.example.expensetracker.adapter.TransitionAdapter
import com.example.expensetracker.daos.ExpenseDao
import com.example.expensetracker.databinding.ActivityShowTransactionListBinding
import com.example.expensetracker.model.TransactionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class ShowTransactionListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowTransactionListBinding
    private var transactionList = mutableListOf<TransactionModel>()
    private lateinit var adapter: TransitionAdapter
    private val expenseDao = ExpenseDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowTransactionListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        getTransactionList(monthName!!)

        val monthsName = resources.getStringArray(R.array.months_name)
        val arrayAdapter = ArrayAdapter(this,R.layout.dropdown_item_layout,monthsName)
        binding.autoCompleteSpinner.setAdapter(arrayAdapter)
        binding.autoCompleteSpinner.setOnItemClickListener { parent, view, position, id ->
            val selectedMonth:String = monthsName[position]
            getTransactionList(selectedMonth)
        }
        binding.transactionList.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
    }

    //deleteItem from list
    private fun deleteItem(transactionModel: TransactionModel, pos: Int){
        FirebaseFirestore.getInstance()
            .collection("expenses")
            .document(transactionModel.expenseId)
            .delete()
            .addOnSuccessListener {
                transactionList.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                getTransactionList(transactionModel.monthName)
                Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show()
                Log.d("delete",transactionModel.expenseId)
            }.addOnFailureListener {
                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            }
    }


    //Showing list of Transaction
    private fun getTransactionList(monthName: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expenseList = expenseDao.getExpenseList(monthName).await()
                val updatedTransactionList = mutableListOf<TransactionModel>()
                for (document in expenseList) {
                    val transactionModelData = document.toObject(TransactionModel::class.java)
                    transactionModelData.expenseId = document.id
                    updatedTransactionList.add(transactionModelData)
                }

                withContext(Dispatchers.Main) {
                    transactionList.clear()
                    transactionList.addAll(updatedTransactionList)
                    adapter =
                        TransitionAdapter(transactionList,object : OnItemClickListener {
                            override fun onItemClick(
                                transactionModel: TransactionModel,
                                pos: Int
                            ) {
                                deleteItem(transactionModel, pos)
                            }
                        })
                    binding.transactionList.adapter = adapter
                    adapter.notifyDataSetChanged()
                    progressDialog.dismiss()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("tag", "exception $e")
                }
            }
        }
    }

}
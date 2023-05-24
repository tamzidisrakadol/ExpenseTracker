package com.example.expensetracker.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.expensetracker.daos.ExpenseDao
import com.example.expensetracker.databinding.ActivityAddExpenseBinding
import com.example.expensetracker.model.TransactionModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitBtn.setOnClickListener {
            createTransition()
        }
        binding.testBTn.setOnClickListener {
            val intent = Intent(this@AddExpenseActivity,FetchDataByDateWise::class.java)
            startActivity(intent)
        }

    }


    private fun createTransition(){
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val expenseId = UUID.randomUUID().toString()
        val amount = binding.amountEditText.text.toString()
        val note = binding.noteEditText.text.toString()
        val category = binding.categoryEditText.text.toString()
        var type:String = ""

        val isIncomeRadioChecked:Boolean =binding.incomeRadioBtn.isChecked
        if (isIncomeRadioChecked){
            type = "income"
        }else{
            type="expense"
        }


        if (amount.isNotEmpty() && note.isNotEmpty() && category.isNotEmpty()){
           val transactionModel = TransactionModel(userID,expenseId,amount.toLong(),note,category,type)
            val expenseDao = ExpenseDao()
            expenseDao.addExpense(transactionModel)
            Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@AddExpenseActivity,MainActivity::class.java)
            startActivity(intent)
            finish()

        }else{
            Toast.makeText(this, "Please fill all the requirements", Toast.LENGTH_SHORT).show()
            return
        }
    }
}
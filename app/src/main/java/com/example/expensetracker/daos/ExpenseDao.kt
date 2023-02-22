package com.example.expensetracker.daos

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.expensetracker.model.Transition
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class ExpenseDao {

    private val db= FirebaseFirestore.getInstance()
    private val expenseCollection = db.collection("expenses")


    fun addExpense(transition: Transition){
        GlobalScope.launch {
            expenseCollection.add(transition)
        }
    }
}
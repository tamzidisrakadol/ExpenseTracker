package com.example.expensetracker.daos

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.expensetracker.model.Transition
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Month
import java.util.Calendar
import java.util.Date

class ExpenseDao {

    private val db= FirebaseFirestore.getInstance()
    private val expenseCollection = db.collection("expenses")


    fun addExpense(transition: Transition){
        GlobalScope.launch {
            expenseCollection.add(transition)
        }
    }

    fun getExpensesForMonth(month: Int):Task<QuerySnapshot>{

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH,month)

        val startOfMonth = calendar.time
        calendar.add(Calendar.MONTH,1)
        val endOfMonth = calendar.time

        return expenseCollection
            .whereGreaterThanOrEqualTo("date",startOfMonth)
            .whereLessThan("date",endOfMonth)
            .get()

    }




}
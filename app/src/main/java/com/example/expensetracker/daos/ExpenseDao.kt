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

    private val db = FirebaseFirestore.getInstance()
    private val expenseCollection = db.collection("expenses")


    fun addExpense(transition: Transition) {
        GlobalScope.launch {
            expenseCollection.add(transition)
        }
    }

//    fun getExpensesForMonth(month: Int): Task<QuerySnapshot> {
//
//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.MONTH, month)
//
//        val startOfMonth = calendar.time
//        calendar.add(Calendar.MONTH, 1)
//        val endOfMonth = calendar.time
//
//        return expenseCollection
//            .whereGreaterThanOrEqualTo("date", startOfMonth)
//            .whereLessThan("date", endOfMonth)
//            .get()
//
//    }

    fun getExpenseList(): Task<QuerySnapshot> {
        return expenseCollection
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get()
    }


    fun displayRemainBalance(transition: List<Transition>): Long {
        var income = 0L
        var expense = 0L
        transition.forEach {
            if (it.type == "income") {
                income += it.amount
            } else {
                expense += it.amount
            }
        }
        var remainingBalance = income - expense
        return remainingBalance
    }


    fun deleteAllData(): Task<QuerySnapshot> {
        return expenseCollection
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result) {
                    document.reference.delete()
                }
            }
        }.addOnFailureListener {
            Log.d("tag", "$it")
        }
    }




}
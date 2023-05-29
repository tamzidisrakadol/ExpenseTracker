package com.example.expensetracker.daos

import android.util.Log
import com.example.expensetracker.model.TransactionModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ExpenseDao {

    private val db = FirebaseFirestore.getInstance()
    private val expenseCollection = db.collection("expenses")


    fun addExpense(transactionModel: TransactionModel) {
        GlobalScope.launch {
            expenseCollection.add(transactionModel)
        }
    }


    fun getExpenseList(monthName:String): Task<QuerySnapshot> {
        return expenseCollection
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .whereEqualTo("monthName", monthName)
            .get()
    }


    fun displayRemainBalance(transactionModel: List<TransactionModel>): Long {
        var income = 0L
        var expense = 0L
        transactionModel.forEach {
            if (it.type == "income") {
                income += it.amount
            } else {
                expense += it.amount
            }
        }
        return income - expense
    }


    fun deleteAllTransaction(monthName:String): Task<QuerySnapshot> {
        return expenseCollection
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .whereEqualTo("monthName",monthName)
            .get()
            .addOnCompleteListener {
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
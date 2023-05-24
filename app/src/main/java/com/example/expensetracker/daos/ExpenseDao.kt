package com.example.expensetracker.daos

import android.util.Log
import com.example.expensetracker.model.Transition
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ExpenseDao {

    private val db = FirebaseFirestore.getInstance()
    private val expenseCollection = db.collection("expenses")


    fun addExpense(transition: Transition) {
        GlobalScope.launch {
            expenseCollection.add(transition)
        }
    }


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
        return income - expense
    }


    fun deleteAllTransaction(): Task<QuerySnapshot> {
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
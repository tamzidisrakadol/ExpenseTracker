package com.example.expensetracker.model

import com.google.firebase.Timestamp

data class TransactionModel(
    val uid:String="",
    var expenseId:String = "",
    val amount:Long = 0L,
    val note:String = "",
    val category:String ="",
    val type:String ="",
    val monthName:String ="",
    val date: Timestamp = Timestamp.now()
)
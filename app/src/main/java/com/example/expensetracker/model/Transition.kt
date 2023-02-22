package com.example.expensetracker.model

import java.util.Date

data class Transition(
    val expenseId:String = "",
    val amount:Long = 0L,
    val note:String = "",
    val category:String ="",
    val type:String ="",
    val date:Date
)
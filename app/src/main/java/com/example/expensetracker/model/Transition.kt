package com.example.expensetracker.model



data class Transition(
    val uid:String="",
    val expenseId:String = "",
    val amount:Long = 0L,
    val note:String = "",
    val category:String ="",
    val type:String ="",
)
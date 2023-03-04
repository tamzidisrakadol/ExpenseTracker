package com.example.expensetracker.model

import com.google.firebase.Timestamp

data class TestDataModel(
    val uid:String="",
    val dataId:String ="",
    val nData:String= "",
    val categoryData:String="",
    val date: Timestamp = Timestamp.now()
)
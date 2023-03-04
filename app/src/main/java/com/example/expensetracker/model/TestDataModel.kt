package com.example.expensetracker.model

import com.google.firebase.Timestamp

data class TestDataModel(
    val uid:String="",
    val dataId:String ="",
    val categoryData:String="",
    val newData:String = "",
    val month:String = "",
    val date: Timestamp = Timestamp.now()
)
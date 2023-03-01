package com.example.expensetracker.model

import com.google.firebase.Timestamp
import java.util.Date

data class TestDataModel(
    val userID:String="",
    val dataId:String ="",
    val nData:String="",
    val categoryData:String="",
    val date: Timestamp = Timestamp.now()
)
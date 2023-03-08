package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.*


@Entity(tableName = "ReminderTable")
data class ReminderDataModel(
    @PrimaryKey(autoGenerate = true)
    val reminderDataId:Int=0,

    @ColumnInfo(name = "reminder_data")
    var reminderData:String="",

    @ColumnInfo(name = "reminder_category")
    var reminderCategory:String="",

    @ColumnInfo(name = "reminder_date")
    var reminderDate: Date,

    @ColumnInfo(name = "reminder_time")
    var reminderTime:Date,
    )
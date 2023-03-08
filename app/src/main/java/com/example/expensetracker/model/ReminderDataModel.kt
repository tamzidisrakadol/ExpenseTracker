package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import java.sql.Time


@Entity(tableName = "ReminderTable")
data class ReminderDataModel(
    @PrimaryKey(autoGenerate = true)
    val reminderDataId:Long=0L,

    @ColumnInfo(name = "reminder_data")
    var reminderData:String="",

    @ColumnInfo(name = "reminder_category")
    var reminderCategory:String="",

    @ColumnInfo(name = "reminder_date")
    var reminderDate:Timestamp = Timestamp.now(),

    @ColumnInfo(name = "reminder_time")
    var reminderTime:Timestamp = Timestamp.now(),
    )
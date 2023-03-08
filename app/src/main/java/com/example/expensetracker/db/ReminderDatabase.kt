package com.example.expensetracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.expensetracker.daos.ReminderDataDao
import com.example.expensetracker.model.ReminderDataModel

@Database(entities = [ReminderDataModel::class], version = 1)
abstract class ReminderDatabase:RoomDatabase() {

    abstract fun reminderDao():ReminderDataDao



    companion object{
        @Volatile
        private var INSTANCE:ReminderDatabase?=null

        fun getDatabase(context: Context):ReminderDatabase{
            if (INSTANCE==null){
                synchronized(this){
                    INSTANCE= Room.databaseBuilder(context,
                        ReminderDatabase::class.java,
                        "ReminderDB")
                        .build()
                }
            }
            return INSTANCE!!
        }

    }
}
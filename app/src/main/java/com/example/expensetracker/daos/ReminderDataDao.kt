package com.example.expensetracker.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.expensetracker.model.ReminderDataModel


@Dao
interface ReminderDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReminderData(reminderDataModel: ReminderDataModel)

    @Query("SELECT * FROM ReminderTable")
    suspend fun getReminder():List<ReminderDataModel>
}
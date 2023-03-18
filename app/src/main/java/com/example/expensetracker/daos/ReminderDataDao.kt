package com.example.expensetracker.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.expensetracker.model.ReminderDataModel


@Dao
interface ReminderDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReminderData(reminderDataModel: ReminderDataModel)

    @Query("SELECT * FROM ReminderTable")
    fun getReminder():LiveData<List<ReminderDataModel>>

    @Delete
    suspend fun deleteItem(reminderDataModel: ReminderDataModel)
}
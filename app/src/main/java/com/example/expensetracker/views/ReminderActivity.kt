package com.example.expensetracker.views

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.RoomDatabase
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityReminderBinding
import com.example.expensetracker.db.ReminderDatabase
import com.example.expensetracker.model.ReminderDataModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderBinding
    private lateinit var db : ReminderDatabase
    private var dataChange: Boolean = false
    private var timeChange: Boolean = false
    private lateinit var castToDate: Date
    private lateinit var castToTime:Date


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db=ReminderDatabase.getDatabase(this)

        binding.selectDateTV.setOnClickListener {
            pickUpDate(it)
        }

        binding.selectTimeTV.setOnClickListener {
            pickUpTime(it)
        }

        binding.saveBtn.setOnClickListener {
            saveDataToRoom()
        }


    }


    private fun pickUpDate(view: View) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDate ->
                val selectDate = "$selectedDate/${selectedMonth + 1}/$selectedYear"
                binding.selectDateTV.text = selectDate
                val sdf = SimpleDateFormat("dd/MM/yyy", Locale.ENGLISH)
                castToDate = sdf.parse(selectDate) as Date
                dataChange = true
            },
            year, month, day
        )
        dpd.show()
    }

    private fun pickUpTime(view: View) {
        val myCalendar = Calendar.getInstance()
        val selectedHour = myCalendar.get(Calendar.HOUR)
        val selectedMin = myCalendar.get(Calendar.MINUTE)


        val mTimePicker = TimePickerDialog(this,
            { view, hourOfDay, minute ->
                val selectedTime = "$hourOfDay:$minute"
                binding.selectTimeTV.text=selectedTime
                timeChange = true
                val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                castToTime = sdf.parse(selectedTime) as Date

            }, selectedHour, selectedMin, true
        )
        mTimePicker.show()
    }


    private fun saveDataToRoom(){
        val reminderData=binding.reminderDataET.text.toString()
        val reminderCategory = binding.reminderCategoryET.text.toString()

        if (reminderData.isNotEmpty() && reminderCategory.isNotEmpty() && dataChange && timeChange){
            val reminderDataModel = ReminderDataModel(0,reminderData,reminderCategory,castToDate,castToTime)
            lifecycleScope.launch {
                db.reminderDao().insertReminderData(reminderDataModel)
                val mProgressDialog = ProgressDialog(this@ReminderActivity)
                mProgressDialog.setTitle("keep patience")
                mProgressDialog.setMessage("Saving....")
                mProgressDialog.show()
                delay(2000L)
                Toast.makeText(this@ReminderActivity, "successfully inserted", Toast.LENGTH_SHORT).show()
                mProgressDialog.hide()
                finish()

            }
        }else{
            Toast.makeText(this@ReminderActivity, "please fill all the requirements", Toast.LENGTH_SHORT).show()
        }
    }

}


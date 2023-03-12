package com.example.expensetracker.views

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityReminderBinding
import com.example.expensetracker.db.ReminderDatabase
import com.example.expensetracker.model.ReminderDataModel
import com.example.expensetracker.services.AlarmReceiver
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
    private val CHANNEL_ID = "ChannelId"
    private val CHANNEL_NAME = "channelname"
    private val NOTIFICATIONID = 0
    private lateinit var notification:Notification
    private lateinit var  notificationManager: NotificationManagerCompat
    private lateinit var alarmManager: AlarmManager
    private var year:Int=0
    private var month:Int=0
    private var day:Int=0
    private var selectedHour:Int=0
    private var selectedMin:Int=0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db=ReminderDatabase.getDatabase(this)

        createNotification()
        notification = NotificationCompat.Builder(this@ReminderActivity,CHANNEL_ID)
            .setContentTitle("Successful")
            .setContentText("Reminder has been saved")
            .setSmallIcon(R.drawable.data_save)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()


        binding.selectDateTV.setOnClickListener {
            pickUpDate(it)
        }

        binding.selectTimeTV.setOnClickListener {
            pickUpTime(it)
        }

        binding.saveBtn.setOnClickListener {
            saveDataToRoom()
            setAlarm()
        }




    }


    private fun pickUpDate(view: View) {
        val myCalendar = Calendar.getInstance()
        year = myCalendar.get(Calendar.YEAR)
         month = myCalendar.get(Calendar.MONTH)
         day = myCalendar.get(Calendar.DAY_OF_MONTH)


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
         selectedHour = myCalendar.get(Calendar.HOUR)
         selectedMin = myCalendar.get(Calendar.MINUTE)


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
                delay(3000L)
                mProgressDialog.dismiss()
                //showing notification after saving data to ROOM
                notificationManager = NotificationManagerCompat.from(this@ReminderActivity)
                if (ActivityCompat.checkSelfPermission(
                        this@ReminderActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@launch
                }
                notificationManager.notify(NOTIFICATIONID,notification)
                finish()

            }
        }else{
            Toast.makeText(this@ReminderActivity, "please fill all the requirements", Toast.LENGTH_SHORT).show()
        }
    }


    //creating notification channel
    private fun createNotification(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

    }

    private fun setAlarm(){
        val timeZone = TimeZone.getDefault()
        val alarmCalendar = Calendar.getInstance(timeZone)
        alarmCalendar.set(Calendar.YEAR,year)
        alarmCalendar.set(Calendar.MONTH,month)
        alarmCalendar.set(Calendar.DAY_OF_MONTH,day)
        alarmCalendar.set(Calendar.HOUR_OF_DAY,selectedHour)
        alarmCalendar.set(Calendar.MINUTE,selectedMin)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this@ReminderActivity,AlarmReceiver::class.java)
        val pendingIntent=PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,alarmCalendar.timeInMillis,pendingIntent)

    }


}


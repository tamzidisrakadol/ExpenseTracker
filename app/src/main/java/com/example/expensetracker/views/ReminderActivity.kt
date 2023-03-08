package com.example.expensetracker.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityReminderBinding

class ReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
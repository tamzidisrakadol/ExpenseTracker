package com.example.expensetracker.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityRecieptTrackingBinding

class ReceiptTrackingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRecieptTrackingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecieptTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}
package com.example.expensetracker.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityShowTransactionListBinding

class ShowTransactionListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowTransactionListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowTransactionListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val monthsName = resources.getStringArray(R.array.months_name)
        val arrayAdapter = ArrayAdapter(this,R.layout.dropdown_item_layout,monthsName)
        binding.autoCompleteSpinner.setAdapter(arrayAdapter)
    }
}
package com.example.expensetracker.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityLoginBinding
import com.example.expensetracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //expenseBtn
        binding.expenseBtn.setOnClickListener {
            val intent = Intent(this@MainActivity,AddExpenseActivity::class.java)
            intent.putExtra("type","expense")
            startActivity(intent)
        }

        //incomeBtn
        binding.incomeBtn.setOnClickListener {
            val intent = Intent(this@MainActivity,AddExpenseActivity::class.java)
            intent.putExtra("type","income")
            startActivity(intent)

        }
    }
}
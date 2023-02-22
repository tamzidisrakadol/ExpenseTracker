package com.example.expensetracker.views

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import com.example.expensetracker.daos.ExpenseDao
import com.example.expensetracker.databinding.ActivityAddExpenseBinding
import com.example.expensetracker.model.Transition
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var dateToSdf:Date
    var type:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent.getStringExtra("type").toString()

        if (type == "income"){
            binding.incomeRadioBtn.isChecked
        }else{
            binding.expenseRadioBtn.isChecked
        }

        binding.expenseRadioBtn.setOnClickListener {
            type = "expense"
        }

        binding.incomeRadioBtn.setOnClickListener {
            type = "income"
        }






        binding.selectDateTV.setOnClickListener {
            pickUpDate(it)
        }

        binding.submitBtn.setOnClickListener {
            createTransition()
        }

    }


    private fun pickUpDate(view: View) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)


        //open the calendar
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->

                var selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                binding.selectDateTV.text = selectedDate


                val sdf = SimpleDateFormat("dd/MM/yyy", Locale.ENGLISH)
                 dateToSdf = sdf.parse(selectedDate) as Date

            },
            year, month, day
        )

        //setting the max date in Calendar
        dpd.datePicker.maxDate = Date().time - 86400000
        dpd.show()
    }

    private fun createTransition(){
        val expenseId = UUID.randomUUID().toString()
        val amount = binding.amountEditText.text.toString()
        val note = binding.noteEditText.text.toString()
        val category = binding.categoryEditText.text.toString()

        val isIncomeRadioChecked:Boolean =binding.incomeRadioBtn.isChecked
        if (isIncomeRadioChecked){
            type = "income"
        }else{
            type="expense"
        }


        if (amount.isNotEmpty() && note.isNotEmpty() && category.isNotEmpty()){
           val transition = Transition(expenseId,amount.toLong(),note,category,type,dateToSdf)
            val expenseDao = ExpenseDao()
            expenseDao.addExpense(transition)
            Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            return
        }
    }
}
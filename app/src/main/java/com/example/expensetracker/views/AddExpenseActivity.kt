package com.example.expensetracker.views

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import com.example.expensetracker.daos.ExpenseDao
import com.example.expensetracker.databinding.ActivityAddExpenseBinding
import com.example.expensetracker.model.Transition
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var dateToSdf:Date
    val myCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.selectDateTV.setOnClickListener {
            pickUpDate(it)
        }

        binding.submitBtn.setOnClickListener {
            createTransition()
        }

    }


    private fun pickUpDate(view: View) {

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
                myCalendar.set(year,month,day)
                dateToSdf= myCalendar.time


            },
            year, month, day
        )

        //setting the max date in Calendar
        dpd.datePicker.maxDate = Date().time - 86400000
        dpd.show()
    }

    private fun createTransition(){
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val expenseId = UUID.randomUUID().toString()
        val amount = binding.amountEditText.text.toString()
        val note = binding.noteEditText.text.toString()
        val category = binding.categoryEditText.text.toString()
        var type:String = ""
        val isIncomeRadioChecked:Boolean =binding.incomeRadioBtn.isChecked
        if (isIncomeRadioChecked){
            type = "income"
        }else{
            type="expense"
        }


        if (amount.isNotEmpty() && note.isNotEmpty() && category.isNotEmpty()){
           val transition = Transition(userID,expenseId,amount.toLong(),note,category,type)
            val expenseDao = ExpenseDao()
            expenseDao.addExpense(transition)
            Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()


            val intent = Intent(this@AddExpenseActivity,MainActivity::class.java)
            startActivity(intent)
            finish()

        }else{
            return
        }
    }
}
package com.example.expensetracker.views

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.expensetracker.R
import com.google.firebase.Timestamp
import com.example.expensetracker.daos.ExpenseDao
import com.example.expensetracker.databinding.ActivityAddExpenseBinding
import com.example.expensetracker.model.TransactionModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding
    private var isDateChanged: Boolean = false
    private var isCategorySelected: Boolean = false
    private lateinit var castToDate: Date
    private var selectedCategory: String = ""
    private var selectMonthFromCalendar:String =""
    private val expenseDao = ExpenseDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitBtn.setOnClickListener {
            createTransition()
        }
        binding.dateSelectTV.setOnClickListener {
            pickUpDate(it)
        }

        binding.button.setOnClickListener {
            startActivity(Intent(this@AddExpenseActivity, FetchDataByDateWise::class.java))
        }

        //selectCategory
        val categoryList = resources.getStringArray(R.array.category_section)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item_layout, categoryList)
        binding.categorySelectTV.setAdapter(arrayAdapter)
        binding.categorySelectTV.setOnItemClickListener { parent, view, position, id ->
            selectedCategory = categoryList[position]
            isCategorySelected = true
        }
    }


    //create transaction
    private fun createTransition() {
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val expenseId = UUID.randomUUID().toString()
        val amount = binding.amountEditText.text.toString()
        val note = binding.noteEditText.text.toString()
        val currentMonthName = Calendar.getInstance()
            .getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        var type: String = ""

        val isIncomeRadioChecked: Boolean = binding.incomeRadioBtn.isChecked
        if (isIncomeRadioChecked) {
            type = "income"
        } else {
            type = "expense"
        }


        if (amount.isNotEmpty() && note.isNotEmpty() && isCategorySelected && isDateChanged) {
            if (selectMonthFromCalendar==currentMonthName){
                val transactionModel = TransactionModel(
                    userID,
                    expenseId,
                    amount.toLong(),
                    note,
                    selectedCategory,
                    type,
                    currentMonthName,
                    Timestamp(castToDate)
                )

                expenseDao.addExpense(transactionModel)
                Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddExpenseActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val transactionModel = TransactionModel(
                    userID,
                    expenseId,
                    amount.toLong(),
                    note,
                    selectedCategory,
                    type,
                    selectMonthFromCalendar,
                    Timestamp(castToDate)
                )

                expenseDao.addExpense(transactionModel)
                Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddExpenseActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Toast.makeText(this, "Please fill all the requirements", Toast.LENGTH_SHORT).show()
            return
        }
    }


    //pickupDate
    private fun pickUpDate(view: View) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDate ->
                val selectDate = "$selectedDate/${selectedMonth + 1}/$selectedYear"
                val sdf = SimpleDateFormat("dd/MM/yyy", Locale.ENGLISH)
                castToDate = sdf.parse(selectDate) as Date
                binding.dateSelectTV.text = selectDate
                isDateChanged = true
                val selectedCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDate)
                }
                selectMonthFromCalendar = SimpleDateFormat("MMMM", Locale.ENGLISH).format(selectedCalendar.time)
            },
            year, month, day
        )

        dpd.datePicker.maxDate = Date().time - 86400000
        dpd.show()
    }
}
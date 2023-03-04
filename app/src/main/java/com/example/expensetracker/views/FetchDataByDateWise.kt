package com.example.expensetracker.views

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.adapter.TestDataAdapter
import com.example.expensetracker.databinding.ActivityFetchDataByDateWiseBinding
import com.example.expensetracker.model.TestDataModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FetchDataByDateWise : AppCompatActivity() {
    private lateinit var binding: ActivityFetchDataByDateWiseBinding
    private var isDateChanged:Boolean = false
    private lateinit var castToDate:Date
    private var testDataModelList = mutableListOf<TestDataModel>()
    private lateinit var testDataAdapter: TestDataAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFetchDataByDateWiseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        testDataAdapter = TestDataAdapter(testDataModelList)
        binding.fetchDataRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)


        binding.dateTV.setOnClickListener {
            pickUpDate(it)
        }
        binding.postBtn.setOnClickListener {
            uploadDataToFireStore()
        }

    }

    private fun pickUpDate(view:View){
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDate ->
            val selectDate = "$selectedDate/${selectedMonth+1}/$selectedYear"
            binding.dateTV.text=selectDate
            val sdf = SimpleDateFormat("dd/MM/yyy", Locale.ENGLISH)
            castToDate = sdf.parse(selectDate) as Date
            isDateChanged = true
        },year,month,day)

        dpd.datePicker.maxDate=Date().time-86400000
        dpd.show()
    }



    private fun uploadDataToFireStore(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val dataId = UUID.randomUUID().toString()
        val nData = binding.testDataEditText.text.toString()
        val categoryData = binding.categoryTestET.text.toString()

        if (nData.isNotEmpty() && categoryData.isNotEmpty() && isDateChanged){
            testDocument(userId,dataId,nData,categoryData,castToDate)
            Toast.makeText(this@FetchDataByDateWise, "successfully uploaded", Toast.LENGTH_SHORT).show()
            binding.testDataEditText.setText("")
            binding.categoryTestET.setText("")
            binding.dateTV.text = "---SELECT DATE---"
            isDateChanged = false

        }else{
            Toast.makeText(this@FetchDataByDateWise, "plz fill all the requirement", Toast.LENGTH_SHORT).show()
            return

        }
    }


    private fun testDocument(userId:String,id:String,nData:String,nCategory:String,date:Date){
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("testData")
        val testDataModel = TestDataModel(userId,id,nData,nCategory, Timestamp(date))

        lifecycleScope.launch {
            collection.add(testDataModel)
        }

    }

    private fun getData(){
        FirebaseFirestore.getInstance()
            .collection("testData")
            .whereEqualTo("uid",FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                for (document in it){
                    val testData = document.toObject(TestDataModel::class.java)
                    testDataModelList.add(testData)
                    binding.fetchDataRecyclerView.adapter = testDataAdapter
                    Log.d("tag","All Data : $testDataModelList")
                }
                testDataAdapter.notifyDataSetChanged()

            }

    }
}
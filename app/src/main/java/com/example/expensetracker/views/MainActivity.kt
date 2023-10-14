package com.example.expensetracker.views

/* TODO: Expense Tracker
    1) user will be notify in the 28th day  that how many money he saves.
    2)Receipt tracking
    3)Ui update in Check network connection
    4)bug fix in reminder class
*/

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.expensetracker.R
import com.example.expensetracker.daos.ExpenseDao
import com.example.expensetracker.databinding.ActivityMainBinding
import com.example.expensetracker.model.TransactionModel
import com.example.expensetracker.model.User
import com.example.expensetracker.utils.NetworkConnection
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var networkConnection:NetworkConnection
    private var transactionModelModelList = mutableListOf<TransactionModel>()
    private val expenseDao = ExpenseDao()
    private var income: Long = 0L
    private var expense: Long = 0L
    private var balance:Long =0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkConnection = NetworkConnection(applicationContext)

        loadProfilePic()

        //add transitionBtn
        binding.transitionBtn.setOnClickListener {
        showBottomDialog()
        }


        binding.imgView.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }

        setUpFireStore()
    }

    override fun onResume() {
        super.onResume()
            income = 0L
            expense = 0L
            setUpFireStore()


    }

    override fun onStart() {
        super.onStart()
        networkConnection.observe(this){
                if (it){
   //                 val snackbar = Snackbar.make(binding.main,"network connection restored",Snackbar.LENGTH_SHORT)
   //                 snackbar.show()
                }else{
                    val snackbar = Snackbar.make(binding.main,"network connection lost",Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
        }

    }


    //get all transaction data by month-wise
    private fun setUpFireStore() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val monthName = Calendar.getInstance()
                    .getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                val expenseList = expenseDao.getExpenseList(monthName!!).await()

                withContext(Dispatchers.Main) {
                    transactionModelModelList.clear()
                    income = 0L
                    expense = 0L

                    for (document in expenseList) {
                        val transitions = document.toObject(TransactionModel::class.java)
                        transitions.expenseId = document.id

                        if (transitions.type == "income") {
                            income += transitions.amount
                        } else {
                            expense += transitions.amount
                        }

                        transactionModelModelList.add(transitions)
                    }

                    balance = expenseDao.displayRemainBalance(transactionModelModelList)
                    if (balance > 0) {
                        binding.remainBalanceTV.text = "TK $balance "
                    } else {
                        binding.remainBalanceTV.text = "TK 0"
                    }
                    setupPieChart()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("tag", "$e")
                }
            }
        }
    }


    //pie-chart showing
    private fun setupPieChart() {
        val pieList = mutableListOf<PieEntry>()
        val colorList = mutableListOf<Int>()

        if (income != 0L) {
            pieList.add(PieEntry(balance.toFloat(), "balance"))
            colorList.add(resources.getColor(R.color.Teal_green))
        }

        if (expense != 0L) {
            pieList.add(PieEntry(expense.toFloat(), "expense"))
            colorList.add(resources.getColor(R.color.scarlet_red))
        }
        val balance = income - expense

        val pieDataSet = PieDataSet(pieList, balance.toString())
        pieDataSet.colors = colorList
        val pieData = PieData(pieDataSet)

        binding.piechart.data = pieData
        binding.piechart.invalidate()
    }


    //profile pic from google
    private fun loadProfilePic() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = expenseDao.getUserInfo()
                if (users.exists()) {
                    val user = users.toObject(User::class.java)
                    val imgUrl = user!!.imgUrl
                    val userName = user.displayName


                    withContext(Dispatchers.Main) {
                        Glide
                            .with(this@MainActivity)
                            .load(imgUrl)
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(binding.imgView)
                    }
                } else {
                    Log.d("tag", "No such Document")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("tag", "$e")
                }
            }
        }
    }


    //open bottom-sheet
    private fun showBottomDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_layout)

        val addTransactionlayout = dialog.findViewById<CardView>(R.id.addTransactionCV)
        val showTransactionlayout = dialog.findViewById<CardView>(R.id.showTransactionCV)
        val addReceiptlayout = dialog.findViewById<CardView>(R.id.addreceiptCV)
        val showreceiptlayout = dialog.findViewById<CardView>(R.id.showreceiptlistCV)

        addTransactionlayout.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this,AddExpenseActivity::class.java))
        }

        showTransactionlayout.setOnClickListener {
            startActivity(Intent(this@MainActivity, ShowTransactionListActivity::class.java))
        }

        addReceiptlayout.setOnClickListener {
            startActivity(Intent(this@MainActivity,ReceiptTrackingActivity::class.java))
        }

        showreceiptlayout.setOnClickListener {
            startActivity(Intent(this@MainActivity,ShowReceiptActivity::class.java))
        }


        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }


}





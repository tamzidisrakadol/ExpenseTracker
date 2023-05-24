package com.example.expensetracker.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.expensetracker.R
import com.example.expensetracker.adapter.OnItemClickListener
import com.example.expensetracker.adapter.TransitionAdapter
import com.example.expensetracker.daos.ExpenseDao
import com.example.expensetracker.databinding.ActivityMainBinding
import com.example.expensetracker.model.Transition
import com.example.expensetracker.model.User
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private var transitionModelList = mutableListOf<Transition>()
    private lateinit var adapter: TransitionAdapter

    private val expenseDao = ExpenseDao()
    private var income: Long = 0L
    private var expense: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfilePic()

        adapter = TransitionAdapter(transitionModelList, this, this)

        //add transitionBtn
        binding.transitionBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, AddExpenseActivity::class.java)
            startActivity(intent)

        }

        //sign out btn
        binding.signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        setUpFireStore()
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)


        //reset btn
        binding.resetBtn.setOnClickListener {
            lifecycleScope.launch {
                expenseDao.deleteAllTransaction()
                withContext(Dispatchers.Main){
                    delay(2000L)
                    setUpFireStore()
                    transitionModelList.clear()
                    adapter.notifyDataSetChanged()
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        income = 0L
        expense = 0L
        setUpFireStore()
    }



    private fun setUpFireStore() {
        expenseDao.getExpenseList()
            .addOnSuccessListener {
                transitionModelList.clear()
                income = 0L
                expense = 0L
                for (document in it) {
                    val transitions = document.toObject(Transition::class.java)
                    transitions.expenseId = document.id
                    if (transitions.type=="income"){
                        income += transitions.amount
                    }else{
                        expense += transitions.amount
                    }
                    transitionModelList.add(transitions)
                }
                binding.recyclerView.adapter = adapter
                val balance = expenseDao.displayRemainBalance(transitionModelList)
                if (balance > 0) {
                    binding.remainBalanceTV.text = "TK $balance "
                } else {
                    binding.remainBalanceTV.text = "TK 0"
                }
                adapter.notifyDataSetChanged()
                setupPieChart()
            }

    }

    override fun onItemClick(transition: Transition, pos: Int) {
        FirebaseFirestore.getInstance()
            .collection("expenses")
            .document(transition.expenseId)
            .delete()
            .addOnSuccessListener {
                transitionModelList.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                setUpFireStore()
                Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            }

    }

    private fun setupPieChart() {
        val pieList = mutableListOf<PieEntry>()
        val colorList = mutableListOf<Int>()

        if (income != 0L) {
            pieList.add(PieEntry(income.toFloat(), "income"))
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

    private fun loadProfilePic(){
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val users = FirebaseFirestore.getInstance().collection("users").document(userID)
        users.get().addOnSuccessListener {
            if (it.exists()){
                val user = it.toObject(User::class.java)
                val imgUrl = user!!.imgUrl
                val userName = user.displayName
                Log.d("name","$userName")

                Glide
                    .with(this)
                    .load(imgUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.imgView)
            }else{
                Log.d("tag","No such Document")
            }
        }.addOnFailureListener {
            Log.d("tag","$it")
        }
    }

}





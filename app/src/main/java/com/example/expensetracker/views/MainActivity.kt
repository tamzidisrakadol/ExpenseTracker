package com.example.expensetracker.views

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.adapter.TransitionAdapter
import com.example.expensetracker.daos.ExpenseDao
import com.example.expensetracker.databinding.ActivityLoginBinding
import com.example.expensetracker.databinding.ActivityMainBinding
import com.example.expensetracker.model.Transition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var transitionModelList = mutableListOf<Transition>()
    private lateinit var adapter: TransitionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.transitionBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, AddExpenseActivity::class.java)
            startActivity(intent)
            finish()
        }

        //sign out btn
        binding.signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@MainActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }



        setUpFireStore()
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter= TransitionAdapter(transitionModelList,this)
        binding.recyclerView.adapter = adapter


    }

    private fun setUpFireStore() {
        val expenseDao = ExpenseDao()
//        val firestore = FirebaseFirestore.getInstance()
//        val transDocu = firestore.collection("expenses")
//        transDocu.get().addOnSuccessListener {
//            for (document in it){
//                val transitions  = document.toObject(Transition::class.java)
//                transitionModelList.add(transitions)
//                displayRemainBalance(transitionModelList)
//            }
//            adapter.notifyDataSetChanged()
//        }

        FirebaseFirestore.getInstance()
            .collection("expenses")
            .whereEqualTo("uid",FirebaseAuth.getInstance().uid)
            .get()
            .addOnSuccessListener {
                for (document in it){
                    val transitions  = document.toObject(Transition::class.java)
                transitionModelList.add(transitions)
                displayRemainBalance(transitionModelList)
            }
            adapter.notifyDataSetChanged()
            }

    }


    fun displayRemainBalance(transition: List<Transition>){
        var income = 0L
        var expense = 0L
        transition.forEach {
            if (it.type=="income"){
                income +=it.amount
            }else{
                expense+=it.amount
            }
        }
        var remainingBalance = income-expense
        binding.remainBalance.text=remainingBalance.toString()
    }

}





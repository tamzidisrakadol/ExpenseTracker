package com.example.expensetracker.views

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.adapter.OnItemClickListener
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

class MainActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private var transitionModelList = mutableListOf<Transition>()
    private lateinit var adapter: TransitionAdapter
    val expenseDao = ExpenseDao()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    }

    override fun onResume() {
        super.onResume()
        setUpFireStore()
    }

    private fun setUpFireStore() {
        expenseDao.getExpenseList()
            .addOnSuccessListener {
                transitionModelList.clear()
                for (document in it) {
                    val transitions = document.toObject(Transition::class.java)
                    transitions.expenseId = document.id
                    transitionModelList.add(transitions)
                }
                binding.recyclerView.adapter = adapter
                var balance = expenseDao.displayRemainBalance(transitionModelList)
                if (balance > 0) {
                    binding.remainBalanceTV.text = "TK $balance "
                } else {
                    binding.remainBalanceTV.text = "TK 0"
                }
                adapter.notifyDataSetChanged()
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

}





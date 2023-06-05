package com.example.expensetracker.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.expensetracker.R
import com.example.expensetracker.daos.ExpenseDao
import com.example.expensetracker.databinding.ActivityProfileBinding
import com.example.expensetracker.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val expenseDao = ExpenseDao()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserInfo()

        binding.signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loadUserInfo(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = expenseDao.getUserInfo()
                if (users.exists()) {
                    val user = users.toObject(User::class.java)
                    val imgUrl = user!!.imgUrl
                    val userName = user.displayName
                    val userEmail = user.userEmail

                    withContext(Dispatchers.Main) {
                        Glide
                            .with(this@ProfileActivity)
                            .load(imgUrl)
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(binding.profileImg)

                        binding.userEmail.text = userEmail
                        binding.userName.text = userName
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
}
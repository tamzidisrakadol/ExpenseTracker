package com.example.expensetracker.daos

import com.example.expensetracker.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDaos {

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")


    //save data to FireStore
    fun addUser(user: User?) {
        user?.let {
            GlobalScope.launch {
                userCollection.document(user.uid).set(it)
            }
        }
    }
}

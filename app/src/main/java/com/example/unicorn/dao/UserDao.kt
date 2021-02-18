package com.example.unicorn.dao

import com.example.unicorn.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    val db = FirebaseFirestore.getInstance()
    val usersCollection=db.collection("users")
    fun addUser(user: User?){
        user?.let{
            GlobalScope.launch (Dispatchers.IO){
                usersCollection.document(user.uid).set(it) //user lia and set krdia firebase pe
            }
        }
    }

    fun getUserById(uId:String): Task<DocumentSnapshot>  { //documnet snapshot reads data from firebase basically here its giving current user id
        return usersCollection.document(uId).get()
    }
}
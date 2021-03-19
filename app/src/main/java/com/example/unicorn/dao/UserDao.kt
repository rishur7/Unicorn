package com.example.unicorn.dao

import android.content.Intent
import com.example.unicorn.PostAdapter
import com.example.unicorn.SignInActivity
import com.example.unicorn.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    private lateinit var signin: SignInActivity
    val db = FirebaseFirestore.getInstance()
    val usersCollection=db.collection("users")
    val auth = Firebase.auth
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
    fun logout() {
        val currentUserId = auth.currentUser!!.uid
        auth.signOut()
        usersCollection.document(currentUserId).delete()
    }
}
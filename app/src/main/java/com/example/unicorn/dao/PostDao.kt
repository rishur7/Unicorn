package com.example.unicorn.dao

import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import com.example.unicorn.EditPostActivity
import com.example.unicorn.models.Post
import com.example.unicorn.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    val db= FirebaseFirestore.getInstance()
    val postCollection = db.collection("posts")
    val auth=Firebase.auth
    fun addPost(text:String)
    {
     val currentUserId= auth.currentUser!!.uid
        GlobalScope.launch{
            val userDao=UserDao()
            val user=userDao.getUserById(currentUserId).await().toObject(User::class.java)!! //user class me convert with current user and !! for user nahi hai and post create horhi hai toh crash hojae app
            val currentTime= System.currentTimeMillis()
            val post= Post(text,user,currentTime)
            postCollection.document().set(post)
        }
    }
fun editPost(postId: String , text:String) {
    val currentUserId= auth.currentUser!!.uid
    GlobalScope.launch {
        val post = getPostById(postId).await().toObject(Post::class.java)!!
        post.text = text
        post.createdAt = System.currentTimeMillis()
        postCollection.document(postId).set(post)
    }
}
    fun deletePost(postId:String) {
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(Post::class.java)!!
            val userSearch=post.createdBy.uid
            if(currentUserId==userSearch) {
                postCollection.document(postId).delete()
            }
            }
    }
    fun getPostById(postId:String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get() //will search this id in firebase
    }
    fun updateLikes(postId:String) {
        GlobalScope.launch {
            val currentUserId= auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(Post::class.java)!!
            val isLiked=post.LikedBy.contains(currentUserId)
 // adding users to liked array
            if(isLiked){
                post.LikedBy.remove(currentUserId)
            }
            else {
                post.LikedBy.add(currentUserId)
            }
 postCollection.document(postId).set(post)
        }
    }
}
/* add karna ho jab tab "set" use karna */
package com.example.unicorn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unicorn.dao.PostDao
import com.example.unicorn.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PostClicks {
    private lateinit var postDao: PostDao
    private lateinit var adapter: PostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab.setOnClickListener{
            val intent= Intent(this,CreatePostActivity::class.java)
            startActivity(intent)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        postDao=PostDao()
        val postsCollections=postDao.postCollection
        val query=postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions=FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()

        adapter= PostAdapter(recyclerViewOptions,this)

        recyclerview.adapter=adapter
        recyclerview.layoutManager=LinearLayoutManager(this)
    }
    //in built firebase feature aka needy thing
    override fun onStart(){
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

     override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }
}
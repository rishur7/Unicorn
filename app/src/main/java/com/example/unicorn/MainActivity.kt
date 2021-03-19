package com.example.unicorn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unicorn.dao.PostDao
import com.example.unicorn.dao.UserDao
import com.example.unicorn.models.Post
import com.example.unicorn.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_post.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity(), PostClicks  {
    private lateinit var postDao: PostDao
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userDao: UserDao
    private lateinit var adapter: PostAdapter
    private val auth= Firebase.auth
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

    override fun onDeleteClicked(postId: String) {
        postDao.deletePost(postId)
    }

    override fun onEditClicked(postId: String) {
        val intent= Intent(this,EditPostActivity::class.java)
        GlobalScope.launch {
            val post = postDao.getPostById(postId).await().toObject(Post::class.java)!!
            val currentUserId = auth.currentUser!!.uid
            val userSearch=post.createdBy.uid
            intent.putExtra(EditPostActivity.ID,postId)
            intent.putExtra(EditPostActivity.POST_TEXT,post.text)
            if(currentUserId==userSearch) {
                startActivity(intent)
            }
            overridePendingTransition(R.anim.slide_to_up,R.anim.slide_to_exit_down)
        }

    }
    //-------Main Menu-------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.popup_menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.about -> {
                val intent=Intent(this,About::class.java)
                startActivity(intent)
                true
            }
            R.id.logout -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                googleSignInClient= GoogleSignIn.getClient(this, gso)
                userDao = UserDao()
                userDao.logout()
                googleSignInClient.revokeAccess()
                val intent=Intent(this,SignInActivity::class.java)
                startActivity(intent)
                true
            }
            else-> super.onOptionsItemSelected(item)
        }
    }
}
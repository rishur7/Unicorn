package com.example.unicorn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.unicorn.dao.PostDao
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_edit_post.*

class EditPostActivity : AppCompatActivity() {
    private lateinit var postDao: PostDao
    companion object{
        const val ID = "id"
        const val POST_TEXT="post_text"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)
        val postEdit = intent.getStringExtra(POST_TEXT)
        EditInput.text= Editable.Factory.getInstance().newEditable(postEdit)
    }

    fun submit(view: View) {
        postDao= PostDao()
        val pId = intent.getStringExtra(ID)!!
        val input = EditInput.text.toString().trim()
        if(input.isNotEmpty()){
            postDao.editPost(pId,input)
            finish()
        }
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_to_down,R.anim.slide_to_exit_up)
    }
}
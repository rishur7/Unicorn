package com.example.unicorn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import kotlinx.android.synthetic.main.activity_about.*

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        insta.movementMethod = LinkMovementMethod.getInstance()
        linkedin.movementMethod= LinkMovementMethod.getInstance()
    }
}
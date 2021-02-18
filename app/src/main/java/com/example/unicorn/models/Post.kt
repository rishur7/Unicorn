package com.example.unicorn.models

class Post (
    val text:String="",
    val createdBy:User = User(),
    val createdAt:Long = 0L,
    var LikedBy:ArrayList<String> = ArrayList())
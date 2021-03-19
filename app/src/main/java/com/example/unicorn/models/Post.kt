package com.example.unicorn.models

class Post (
    var text:String="",
    val createdBy:User = User(),
    var createdAt:Long = 0L,
    var LikedBy:ArrayList<String> = ArrayList())
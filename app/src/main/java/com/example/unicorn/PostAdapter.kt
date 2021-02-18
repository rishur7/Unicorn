package com.example.unicorn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unicorn.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostAdapter(options: FirestoreRecyclerOptions<Post>, private val listener:PostClicks) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(options) {
    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val userName:TextView= itemView.findViewById(R.id.userName)
        val createdAt:TextView= itemView.findViewById(R.id.createdAt)
        val postTitle:TextView= itemView.findViewById(R.id.postTitle)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val likeCount:TextView= itemView.findViewById(R.id.likeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder= PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))
        viewHolder.likeButton.setOnClickListener{
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id) //to get post id
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.userName.text=model.createdBy.displayName
        holder.postTitle.text=model.text
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)
        holder.likeCount.text=model.LikedBy.size.toString()
        holder.createdAt.text = TimeSetter.getTimeAgo(model.createdAt)

        val auth= Firebase.auth
        val currentUserId= auth.currentUser!!.uid
        val isLiked= model.LikedBy.contains(currentUserId)
        // adding users to liked array
        if(isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.like))
        }
        else {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.unlike))
        }
    }
}
interface PostClicks {
    fun onLikeClicked(postId:String)
}
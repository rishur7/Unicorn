package com.example.unicorn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import com.example.unicorn.dao.UserDao
import com.example.unicorn.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN:Int = 123
    private val TAG="SignInActivity Tag"
    private lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient= GoogleSignIn.getClient(this, gso)
        auth=Firebase.auth
        signInButton.setOnClickListener{
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        updateUI(currentUser)
    }
    //signin button
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN) //sari email id dikhe uske lie ye intent hai
    }
    //callback after forResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { //request code me id if== available
            val task = GoogleSignIn.getSignedInAccountFromIntent(data) //task ke andr account jo abhi login kia hai
            handleSignInResult(task)
        }
    }
//task me id hogi
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed" + e.statusCode)
        }
    }
//firebase me store
    private fun firebaseAuthWithGoogle(idToken: String) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    signInButton.visibility=GONE
    progbar.visibility=View.VISIBLE
    GlobalScope.launch(Dispatchers.IO) {
   val auth=auth.signInWithCredential(credential).await()
        val firebaseUser= auth.user
        withContext(Dispatchers.Main){
            updateUI(firebaseUser)
        }
    }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
if(firebaseUser!=null){
    val user= User(firebaseUser.uid, firebaseUser.displayName, firebaseUser.photoUrl.toString())
    val userDao=UserDao()
    userDao.addUser(user)
    val mainActivityIntent= Intent(this, MainActivity::class.java)
    startActivity(mainActivityIntent)
    finish()
}
else {
    signInButton.visibility=View.VISIBLE
    progbar.visibility=GONE
        }
    }

    fun revokeAccess() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
     googleSignInClient.revokeAccess()

    }

}
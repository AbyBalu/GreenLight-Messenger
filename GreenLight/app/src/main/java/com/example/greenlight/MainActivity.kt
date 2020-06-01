@file:Suppress("DEPRECATION")

package com.example.greenlight

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    var firebaseUser: FirebaseUser?=null
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID:String=""

    private var rellay1: RelativeLayout? = null
    private var rellay2: RelativeLayout? = null
    private var rellay3: RelativeLayout? = null
    private var rellay4: RelativeLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        login_btn.setOnClickListener{
            loginUser()
        }

        register_btn.setOnClickListener{
            registerUser()
        }

        rellay1 = findViewById(R.id.rellay1)
        rellay2 = findViewById(R.id.rellay2)
        rellay3 = findViewById(R.id.rellay3)
        rellay4 = findViewById(R.id.rellay4)

        signup.setOnClickListener{
                rellay1!!.visibility = View.INVISIBLE
                rellay2!!.visibility = View.INVISIBLE
                rellay3!!.visibility = View.VISIBLE
                rellay4!!.visibility = View.VISIBLE

        }

        signin.setOnClickListener{
                rellay3!!.visibility = View.INVISIBLE
                rellay4!!.visibility = View.INVISIBLE
                rellay1!!.visibility = View.VISIBLE
                rellay2!!.visibility = View.VISIBLE
        }

    }


    private fun loginUser() {
        val progressBar = ProgressDialog(this@MainActivity)
        progressBar.setMessage("Logging in...")
        val email: String = email_login.text.toString()
        val password: String = password_login.text.toString()
        if (email==""){
            Toast.makeText(this@MainActivity,"Please enter email.", Toast.LENGTH_LONG).show()
        }
        else if (password==""){
            Toast.makeText(this@MainActivity,"Please enter password.", Toast.LENGTH_LONG).show()
        }
        else{
            progressBar.show()
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val intent = Intent(this@MainActivity,HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        progressBar.dismiss()
                    }else{
                        Toast.makeText(this@MainActivity,"Error message:"+task.exception!!.message.toString(),
                            Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
    override fun onStart() {
        super.onStart()

        firebaseUser=FirebaseAuth.getInstance().currentUser
        if(firebaseUser!=null){
            val intent = Intent(this@MainActivity,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser() {
        val progressBar = ProgressDialog(this@MainActivity)
        progressBar.setMessage("Registering...")
        val username: String = username_register.text.toString()
        val email: String = email_register.text.toString()
        val password: String = password_register.text.toString()

        if(username==""){
            Toast.makeText(this@MainActivity,"Please enter username.", Toast.LENGTH_LONG).show()
        }
        else if (email==""){
            Toast.makeText(this@MainActivity,"Please enter email.", Toast.LENGTH_LONG).show()
        }
        else if (password==""){
            Toast.makeText(this@MainActivity,"Please enter password.", Toast.LENGTH_LONG).show()
        }
        else{
            progressBar.show()
            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        firebaseUserID=mAuth.currentUser!!.uid
                        refUsers= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)
                        val userHashMap=HashMap<String,Any>()
                        userHashMap["uid"]=firebaseUserID
                        userHashMap["username"]=username
                        userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/greenlight-v1.appspot.com/o/profile.png?alt=media&token=234f38d0-9ce4-4eb8-8e49-30490a7c9af5"
                        userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/greenlight-v1.appspot.com/o/cover1.jpg?alt=media&token=f45d6504-1967-4c15-b25b-00748db72c38"
                        userHashMap["status"]="offline"
                        userHashMap["search"]=username.toLowerCase()
                        userHashMap["facebook"]="https://m.facebook.com"
                        userHashMap["instagram"]="https://m.instagram.com"
                        userHashMap["website"]="https://www.google.com"

                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    val intent = Intent(this@MainActivity,HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        Toast.makeText(this@MainActivity,"Registration was successful.", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this@MainActivity,"Error message:"+task.exception!!.message.toString(),
                            Toast.LENGTH_LONG).show()
                    }
                    progressBar.dismiss()
                }
        }
    }
}

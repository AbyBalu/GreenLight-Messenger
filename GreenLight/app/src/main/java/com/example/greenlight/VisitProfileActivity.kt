package com.example.greenlight

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.greenlight.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_profile.*

class VisitProfileActivity : AppCompatActivity() {

    private  var userVisitId: String=""
    var user: Users? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_profile)

        userVisitId = intent.getStringExtra("visit_id")
        firebaseUser= FirebaseAuth.getInstance().currentUser

        val toolbar: Toolbar = findViewById(R.id.toolbar_profile)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    user = p0.getValue(Users::class.java)

                    username_display.text = user!!.getUserName()
                    Picasso.get().load(user!!.getProfile()).placeholder(R.drawable.profile).into(profile_display)
                    Picasso.get().load(user!!.getCover()).placeholder(R.drawable.side_nav_bar).into(cover_display)
                }
            }
        })

        facebook_display.setOnClickListener {

            val uri = Uri.parse(user!!.getFacebook())
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

        instagram_display.setOnClickListener {

            val uri = Uri.parse(user!!.getInstagram())
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

        website_display.setOnClickListener {

            val uri = Uri.parse(user!!.getWebsite())
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

        send_msg_btn.setOnClickListener{

            val intent = Intent(this@VisitProfileActivity, MessageChatActivity::class.java)
            intent.putExtra("visit_id",user!!.getUID())
            startActivity(intent)
        }
    }
    private fun updateStatus(status: String){

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String,Any>()
        hashMap["status"] = status
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()

        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()

        updateStatus("offline")
    }
}

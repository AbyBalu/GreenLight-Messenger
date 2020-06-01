package com.example.greenlight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ViewFullImageActivity : AppCompatActivity() {

    private var image_viewer: ImageView?= null
    private var imageUrl: String =""
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)

        firebaseUser= FirebaseAuth.getInstance().currentUser

        imageUrl = intent.getStringExtra("url")
        image_viewer = findViewById(R.id.image_viewer)

        Picasso.get().load(imageUrl).into(image_viewer)
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

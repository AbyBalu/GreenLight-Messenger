@file:Suppress("DEPRECATION")

package com.example.greenlight

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.greenlight.ModelClasses.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private var usersReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    private val RequestCode=438
    private var imageUri: Uri? = null
    private  var storageRef: StorageReference?=null
    private var coverChecker: String?=""
    private var socialChecker: String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef= FirebaseStorage.getInstance().reference.child("User Images")

        val toolbar: Toolbar = findViewById(R.id.toolbar_profile)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@ProfileActivity,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        usersReference!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user: Users?= p0.getValue(Users::class.java)
                    if (applicationContext!=null){
                        username_settings.text = user!!.getUserName()
                        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(profile_image_settings)
                        Picasso.get().load(user.getCover()).placeholder(R.drawable.side_nav_bar).into(cover_image_settings)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        set_facebook.setOnClickListener{
            socialChecker = "facebook"
            setSocialLinks()
        }
        set_instagram.setOnClickListener{
            socialChecker = "instagram"
            setSocialLinks()
        }
        set_website.setOnClickListener{
            socialChecker = "website"
            setSocialLinks()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.profile_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.update_profile -> {
                pickImage()
            }
            R.id.update_cover -> {
                coverChecker="cover"
                pickImage()
            }
            R.id.profile_signout -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@ProfileActivity,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                return true
            }
        }
        return true
    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ProfileActivity,R.style.DialogTheme)

        if (socialChecker == "website"){
            builder.setTitle("URL : ")
        }
        else{
            builder.setTitle("Username : ")
        }
        val editText = EditText(this@ProfileActivity)
        editText.setPadding(80,50,80,80)

        if (socialChecker == "website"){
            editText.hint = "eg. www.google.com"
        }
        else{
            editText.hint = "eg. google"
        }
        builder.setView(editText)
        builder.setPositiveButton("Create", DialogInterface.OnClickListener{
                _, _ ->
            val str = editText.text.toString()
            if (str==""){
                Toast.makeText(this@ProfileActivity,"Please write something...",Toast.LENGTH_LONG).show()
            }
            else{
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{
                dialog, _ ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun saveSocialLink(str: String) {
        val mapSocial = HashMap<String,Any>()

        when(socialChecker){

            "facebook"->{
                mapSocial["facebook"]="https://m.facebook.com/$str"
            }
            "instagram"->{
                mapSocial["instagram"]="https://m.instagram.com/$str"
            }
            "website"->{
                mapSocial["website"]="https://$str"
            }
        }
        usersReference!!.updateChildren(mapSocial).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this@ProfileActivity,"Updated Successfully",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type="image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data!=null){
            imageUri=data.data
            Toast.makeText(this@ProfileActivity,"Uploading...",Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {

        val progressBar = ProgressDialog(this@ProfileActivity)
        progressBar.setMessage("Image is uploading, please wait...")
        progressBar.show()

        if (imageUri!=null){
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString()+".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask (Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker=="cover"){
                        val mapCoverImg = HashMap<String,Any>()
                        mapCoverImg["cover"]=url
                        usersReference!!.updateChildren(mapCoverImg)
                        coverChecker= ""
                    }
                    else{
                        val mapProfileImg = HashMap<String,Any>()
                        mapProfileImg["profile"]=url
                        usersReference!!.updateChildren(mapProfileImg)
                        coverChecker= ""
                    }
                    progressBar.dismiss()
                }
            }
        }

    }
    override fun onBackPressed() {
        val intent = Intent(this@ProfileActivity,HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun updateStatus(status: String){

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String,Any>()
        hashMap["status"] = status
        ref.updateChildren(hashMap)
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

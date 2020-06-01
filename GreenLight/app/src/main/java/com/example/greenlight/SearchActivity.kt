package com.example.greenlight


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenlight.AdapterClasses.UserAdapter
import com.example.greenlight.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {


    private var userAdapter: UserAdapter?= null
    private var mUsers: List<Users>?=null
    private var recyclerView: RecyclerView?=null
    private var searchEditText: EditText?=null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        firebaseUser=FirebaseAuth.getInstance().currentUser

        val toolbar: Toolbar = findViewById(R.id.toolbar_friends)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@SearchActivity,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        recyclerView=findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager= LinearLayoutManager(this@SearchActivity)
        searchEditText=findViewById(R.id.searchUsersET)
        mUsers = ArrayList()
        retrieveAllUsers()


        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun retrieveAllUsers() {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers= FirebaseDatabase.getInstance().reference.child("Users")
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                if (searchEditText!!.text.toString()==""){
                    for(snapshot in p0.children){
                        val user: Users?= snapshot.getValue(Users::class.java)
                        if(!(user!!.getUID()).equals(firebaseUserID)){
                            (mUsers as ArrayList<Users>).add(user)
                        }
                    }
                    userAdapter= UserAdapter(this@SearchActivity!!,mUsers!!,false)
                    recyclerView!!.adapter=userAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    private  fun searchForUsers(str: String) {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("search").startAt(str).endAt(str + "\uf8ff")
        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (!(user!!.getUID()).equals(firebaseUserID)) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(this@SearchActivity!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this@SearchActivity,HomeActivity::class.java)
        startActivity(intent)
        finish()
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

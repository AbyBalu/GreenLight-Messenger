package com.example.greenlight

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenlight.AdapterClasses.UserAdapter
import com.example.greenlight.ModelClasses.Chatlist
import com.example.greenlight.ModelClasses.Users
import com.example.greenlight.Notifications.Token
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.nav_header.*


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    private var userAdapter: UserAdapter?= null
    private var mUsers: List<Users>?=null
    private var usersChatList: List<Chatlist>?=null
    lateinit var recycler_view_chatlist: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_home)

        firebaseUser=FirebaseAuth.getInstance().currentUser
        refUsers= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)



        val toolbar: Toolbar = findViewById(R.id.toolbar_home)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        recycler_view_chatlist = findViewById(R.id.recycler_view_chatlist)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager = LinearLayoutManager(this)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navHeader = navView.getHeaderView(0)
        val username = navHeader.findViewById(R.id.username) as TextView
        val profile_img_nav = navHeader.findViewById(R.id.profile_img_nav) as ImageView
        val cover_img_nav = navHeader.findViewById(R.id.cover_img_nav) as ImageView


        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        //display username & profile picture
        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user: Users?=p0.getValue(Users::class.java)
                    username.text=user!!.getUserName()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(profile_img_nav)
                    Picasso.get().load(user.getCover()).placeholder(R.drawable.side_nav_bar).resize(500,250).centerCrop().into(cover_img_nav)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })



        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList=ArrayList()

        val ref =FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (usersChatList as ArrayList).clear()

                for (dataSnapshot in p0.children){
                    val chatlist = dataSnapshot.getValue(Chatlist::class.java)
                    (usersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        updateToken(FirebaseInstanceId.getInstance().token)
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this@HomeActivity,ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.nav_messages -> {

            }
            R.id.nav_friends -> {
                val intent = Intent(this@HomeActivity, SearchActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@HomeActivity,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                return true
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu,menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        val searchClose =  searchView.javaClass.getDeclaredField("mCloseButton")
        searchClose.isAccessible = true
        val closeImage = searchClose.get(searchView) as ImageView
        closeImage.setImageResource(R.drawable.ic_close)
        searchView.setQueryHint("Search...")


        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("",false)
                searchItem.collapseActionView()
                Toast.makeText(this@HomeActivity,"looking for $query",Toast.LENGTH_LONG).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(this@HomeActivity,"looking for $newText",Toast.LENGTH_LONG).show()
                return false
            }
        })
        return true
    }

    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun retrieveChatList(){
        mUsers = ArrayList()

        val ref =FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for (dataSnapshot in p0.children){
                    val user =dataSnapshot.getValue(Users::class.java)
                    for (eachChatList in usersChatList!!){
                        if (user!!.getUID().equals(eachChatList.getId())){
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(this@HomeActivity!!,(mUsers as ArrayList<Users>), true)
                recycler_view_chatlist.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
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

package com.example.greenlight.AdapterClasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.greenlight.ModelClasses.Chat
import com.example.greenlight.R
import com.example.greenlight.ViewFullImageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(
    private val mContext: Context,
    private val mChatlist: List<Chat>,
    private val imageUrl: String
) :RecyclerView.Adapter<ChatsAdapter.ViewHolder?>(){

    var firebaseUser:FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    inner class  ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var profile_image: CircleImageView?= null
        var show_text_message: TextView? = null
        var left_image_view: ImageView?= null
        var text_seen: TextView?= null
        var right_image_view: ImageView?= null

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1){
            val view:View= LayoutInflater.from(mContext).inflate(R.layout.message_item_right,parent,false)
            ViewHolder(view)
        }
        else{
            val view:View= LayoutInflater.from(mContext).inflate(R.layout.message_item_left,parent,false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat:Chat=mChatlist[position]

        Picasso.get().load(imageUrl).into(holder.profile_image)

        //image massages
        if (chat.getMessage().equals("sent you an image") && !chat.getUrl().equals("")){

            //image message - right
            if(chat.getSender().equals(firebaseUser.uid)){
                holder.show_text_message!!.visibility= View.GONE
                holder.right_image_view!!.visibility= View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.right_image_view)

                holder.right_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Delete Image",
                        "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options,DialogInterface.OnClickListener{
                            _, which ->
                        if (which == 0){
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url",chat.getUrl())
                            mContext.startActivity(intent)

                        }
                        else if (which==1){

                            deleteSentMessage(position,holder)
                        }
                    })
                    builder.show()
                }
            }
            //image message - left
            else if(!chat.getSender().equals(firebaseUser.uid)){
                holder.show_text_message!!.visibility= View.GONE
                holder.left_image_view!!.visibility= View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.left_image_view)

                holder.left_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options,DialogInterface.OnClickListener{
                            _, which ->
                        if (which == 0){
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url",chat.getUrl())
                            mContext.startActivity(intent)

                        }
                    })
                    builder.show()
                }
            }
        }
        //text message
        else{
            holder.show_text_message!!.text=chat.getMessage()

            if(firebaseUser.uid == chat.getSender()){

                holder.show_text_message!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Delete Message",
                        "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options,DialogInterface.OnClickListener{
                            _, which ->
                        if (which==0){

                            deleteSentMessage(position,holder)
                        }
                    })
                    builder.show()
                }
            }
        }
        //sent and seen message
        if (position==mChatlist.size-1){
            if (chat.isIsseen()!!){
                holder.text_seen!!.text="Seen"

                if (chat.getMessage().equals("sent you an image") && !chat.getUrl().equals("")){
                    val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
            else{
                holder.text_seen!!.text="Sent"

                if (chat.getMessage().equals("sent you an image") && !chat.getUrl().equals("")){
                    val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
        }
        else{
            holder.text_seen!!.visibility=View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if (mChatlist[position].getSender().equals(firebaseUser.uid)){
            1
        }
        else{
            0
        }
    }

    private fun deleteSentMessage(position: Int, holder: ChatsAdapter.ViewHolder){

        FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatlist[position].getMessageId()!!)
            .removeValue()
            .addOnCompleteListener{task ->

                if (task.isSuccessful){
                    Toast.makeText(holder.itemView.context,"Deleted",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(holder.itemView.context,"Failed, Not deleted",Toast.LENGTH_SHORT).show()
                }
            }
    }
}
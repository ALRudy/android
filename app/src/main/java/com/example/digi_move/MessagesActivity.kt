package com.example.digi_move

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.activity_planifier.*
import kotlinx.android.synthetic.main.layout_message_seen.view.*

class MessagesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var myRef : DatabaseReference
    private lateinit var user_chat : Users
    var user : Users? = null
    private lateinit var muser : FirebaseUser
    private lateinit var adapter : GroupAdapter<ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("regions/")
        adapter = GroupAdapter<ViewHolder>()
        auth = FirebaseAuth.getInstance()
        muser = auth.currentUser!!
        get_user()
        textView_retour.setOnClickListener {
            finish()
        }
        textView_nouveau_message.setOnClickListener {
            val intent = Intent(this, NouveauMessageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getMessages() {

        val ref = FirebaseDatabase.getInstance().getReference("/latest_messages/${user?.id}")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    val msg = it.getValue(Messages::class.java)

                    //Toast.makeText(baseContext,"ok", Toast.LENGTH_LONG).show()
                    if (msg != null){
                        adapter.add(MessageItemSeen(msg))
                    }


                }
                list_latest_message.adapter = adapter
                list_latest_message.scrollToPosition(p0.children.count())
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val msg = p0.getValue(Messages::class.java)

                //Toast.makeText(baseContext,"ok",Toast.LENGTH_LONG).show()
                adapter.add(MessageItemSeen(msg!!))
                list_latest_message.adapter = adapter
                list_latest_message.scrollToPosition(p0.children.count())
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(baseContext,"pas ok", Toast.LENGTH_LONG).show()
            }

        })
    }
    fun get_user(){

        val ref = FirebaseDatabase.getInstance().getReference("users/${muser?.uid}")

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(Users::class.java)
                getMessages()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        ref.addListenerForSingleValueEvent(userListener)

    }
    class MessageItemSeen(val msg : Messages) : Item<ViewHolder>(){
        var user_chat : Users? = null
        override fun getLayout(): Int {
            return R.layout.layout_chat_rec
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            user_chat = GetUserMessage(msg.id_env)
            viewHolder.itemView.textView_message_message_seen.text = "${msg.message}"
            viewHolder.itemView.textView_date_message_seen.text = "${msg.date} ${msg.heure}"
            viewHolder.itemView.textView_name_message_seen.text = "${msg.email}"
            Glide.with(viewHolder.root.context).load(user_chat?.profile).into(viewHolder.itemView.imageView_icon_message_seen)
            if(!msg.lu)viewHolder.itemView.setBackgroundColor(Color.argb(89,255,234,234))

        }

        private fun GetUserMessage(id_env: String?): Users? {
            val ref = FirebaseDatabase.getInstance().getReference("users/${id_env}")
            var user : Users? = null
            val userListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    user = dataSnapshot.getValue(Users::class.java)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            ref.addListenerForSingleValueEvent(userListener)
            return user
        }

    }
}

package com.example.digi_move

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.layout_item_last_message.view.*
import kotlinx.android.synthetic.main.user_item_layout.view.*
import kotlinx.android.synthetic.main.user_item_layout.view.textView_message_last_message
import kotlinx.android.synthetic.main.layout_item_last_message.view.imageView_icon_last_message as imageView_icon_last_message1
import kotlinx.android.synthetic.main.layout_item_last_message.view.textView_name_last_message as textView_name_last_message1

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
    companion object{
        var USER_CHAT = "USER_CHAT"
    }
    private fun refreshadapter() {
        adapter.clear()
        last_message_map.values.forEach {
            adapter.add(MessageItemSeen(it))
        }
        adapter.setOnItemClickListener { item, view ->
            val userItem = item as MessageItemSeen
            val intent = Intent(view.context, ChatActivity::class.java)

            intent.putExtra(USER_CHAT,userItem.user_chat)
            startActivity(intent)
        }
    }
    val last_message_map = HashMap<String,Messages>()
    private fun getMessages() {

        val ref = FirebaseDatabase.getInstance().getReference("/latest_messages/${user?.id}")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                adapter = GroupAdapter<ViewHolder>()
                val msg = p0.getValue(Messages::class.java)?:return

                last_message_map[p0.key!!] = msg
                refreshadapter()

                list_latest_message.adapter = adapter
                list_latest_message.scrollToPosition(0)
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                adapter = GroupAdapter<ViewHolder>()
                val msg = p0.getValue(Messages::class.java)?:return

                last_message_map[p0.key!!] = msg
                refreshadapter()

                list_latest_message.adapter = adapter
                list_latest_message.scrollToPosition(0)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    val msg = it.getValue(Messages::class.java)

                    //Toast.makeText(baseContext,"ok", Toast.LENGTH_LONG).show()
                    if (msg != null){
                        adapter.add(MessageItemSeen(msg))
                    }


                }
                list_latest_message.adapter = adapter
                list_latest_message.scrollToPosition(0)
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
            return R.layout.layout_item_last_message
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val ref = FirebaseDatabase.getInstance().getReference("users/${msg.id_rec}")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    user_chat = p0.getValue(Users::class.java)
                    viewHolder.itemView.textView_name_last_message.text = "${user_chat?.prenom} ${user_chat?.nom}"
                    Glide.with(viewHolder.root.context).load(user_chat?.profile).into(viewHolder.itemView.imageView_icon_last_message)
                }

            })
            viewHolder.itemView.textView_message_last_message.text = ""
            if (msg.id_env == FirebaseAuth.getInstance().currentUser?.uid){
                viewHolder.itemView.textView_message_last_message.text = "vous : "
            }
            viewHolder.itemView.textView_message_last_message.text = viewHolder.itemView.textView_date_last_mesage.text.toString()+ "${msg.message}"
            viewHolder.itemView.textView_date_last_mesage.text = "${msg.date} ${msg.heure}"
            if(!msg.lu)viewHolder.itemView.setBackgroundColor(Color.argb(89,255,234,234))

        }

        private fun GetUserMessage(id_rec: String?): Users? {
            val ref = FirebaseDatabase.getInstance().getReference("users/${id_rec}")
            var user_chat : Users? = null

            val userListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    user_chat = dataSnapshot.getValue(Users::class.java)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            ref.addListenerForSingleValueEvent(userListener)
            return user_chat
        }

    }
}

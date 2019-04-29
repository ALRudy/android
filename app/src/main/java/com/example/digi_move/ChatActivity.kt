package com.example.digi_move

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import khronos.Dates
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.layout_chat_env.view.*
import kotlinx.android.synthetic.main.layout_chat_rec.view.*
import kotlinx.android.synthetic.main.layout_chat_rec.view.textView_date
import kotlinx.android.synthetic.main.layout_chat_rec.view.textView_message

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var user : Users? = null
    private lateinit var user_chat : Users
    private lateinit var muser : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth = FirebaseAuth.getInstance()
        muser = auth.currentUser!!
        get_user()
        user_chat = intent.getParcelableExtra<Users>(NouveauMessageActivity.USER_CHAT)
        toolbar2.title = "${user_chat.prenom}"



        //val adapter = GroupAdapter<ViewHolder>()


        button_envoyer.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("/messages/${user?.id}/${user_chat?.id}").push()
            val ref2 = FirebaseDatabase.getInstance().getReference("/messages/${user_chat?.id}/${user?.id}").push()
            val message = Messages()
            message.id_env = muser?.uid
            message.id_rec = user_chat.id
            message.lu = false
            message.message = edit_text_message_to_send.text.toString()
            message.date=Dates.today.toString()
            message.heure=Dates.today.time.toString()

            val message2 = Messages()
            message2.id_env = user_chat.id
            message2.id_rec = muser?.uid
            message2.lu = false
            message2.message = edit_text_message_to_send.text.toString()
            message2.date=Dates.today.toString()
            message2.heure=Dates.today.time.toString()

            ref.setValue(message).addOnCompleteListener {
                Toast.makeText(baseContext,"Done",Toast.LENGTH_SHORT).show()
            }
            ref2.setValue(message2).addOnCompleteListener {
                Toast.makeText(baseContext,"Done2",Toast.LENGTH_SHORT).show()
                edit_text_message_to_send.text.clear()
            }

        }
        //list_chat_c.adapter = adapter


    }

    private fun fetchChatDialog() {

        val ref = FirebaseDatabase.getInstance().getReference("/messages/${user?.id}/${user_chat?.id}")
        Toast.makeText(baseContext,"/messages/${user?.id}/${user_chat?.id}",Toast.LENGTH_LONG).show()
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(baseContext,"pas ok",Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    val msg = it.getValue(Messages::class.java)

                    Toast.makeText(baseContext,"ok",Toast.LENGTH_LONG).show()
                    if (msg != null){
                        if (msg.getId_env() == user?.id){
                            adapter.add(ChatItemEnv(msg,user!!))
                        }
                        else{
                            adapter.add(ChatItemRec(msg,user_chat))
                        }
                    }


                }
                list_chat_c.adapter = adapter
            }

        })
    }
    fun get_user(){

        val ref = FirebaseDatabase.getInstance().getReference("users/${muser?.uid}")

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(Users::class.java)
                fetchChatDialog()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        ref.addListenerForSingleValueEvent(userListener)




    }
    class ChatItemRec(val msg : Messages,val user_chat :Users) : Item<ViewHolder>(){
        override fun getLayout(): Int {

            return R.layout.layout_chat_rec
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textView_message.text = "${msg.message}"
            viewHolder.itemView.textView_date.text = "${msg.date} ${msg.heure}"
            Glide.with(viewHolder.root.context).load(user_chat.profile).into(viewHolder.itemView.circleImageView_icon_chat_rec)
        }

    }
    class ChatItemEnv(val msg : Messages,val user:Users) : Item<ViewHolder>(){
        override fun getLayout(): Int {

            return R.layout.layout_chat_env
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textView_message.text = "${msg.message}"
            viewHolder.itemView.textView_date.text = "${msg.date} ${msg.heure}"
            Glide.with(viewHolder.root.context).load(user?.profile).into(viewHolder.itemView.circleImageView_icon_chat_env)
        }

    }

}

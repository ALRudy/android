package com.example.digi_move

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.KlockLocale
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import khronos.Dates
import khronos.beginningOfDay
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.layout_chat_env.view.*
import kotlinx.android.synthetic.main.layout_chat_rec.view.*
import kotlinx.android.synthetic.main.layout_chat_rec.view.textView_date
import kotlinx.android.synthetic.main.layout_chat_rec.view.textView_message
import kotlinx.android.synthetic.main.layout_chat_env.view.textView_pseudo as textView_pseudo1

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var user : Users? = null
    private lateinit var user_chat : Users
    private lateinit var muser : FirebaseUser
    private lateinit var adapter : GroupAdapter<ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        adapter = GroupAdapter<ViewHolder>()
        auth = FirebaseAuth.getInstance()
        muser = auth.currentUser!!
        get_user()
        user_chat = intent.getParcelableExtra<Users>(NouveauMessageActivity.USER_CHAT)
        toolbar2.title = "${user_chat.prenom}"



        //val adapter = GroupAdapter<ViewHolder>()


        button_envoyer.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("/messages/${user?.id}/${user_chat?.id}").push()
            Toast.makeText(baseContext,ref.key,Toast.LENGTH_SHORT).show()
            val ref2 = FirebaseDatabase.getInstance().getReference("/messages/${user_chat?.id}/${user?.id}/${ref.key}")
            val date = DateTimeTz.nowLocal()
            val locale = KlockLocale.default
            val message = Messages()
            message.id_message = ref.key
            message.id_env = muser?.uid
            message.id_rec = user_chat.id
            message.lu = true
            message.message = edit_text_message_to_send.text.toString()
            message.date= locale.formatDateMedium.format(date)
            message.heure= locale.formatTimeShort.format(date)
            message.email=muser.email
            message.name="${user?.prenom} ${user?.nom}"

            val message2 = Messages()
            message2.id_message = ref.key
            message2.id_env =  muser?.uid
            message2.id_rec = user_chat.id
            message2.lu = false
            message2.message = edit_text_message_to_send.text.toString()
            message2.date= locale.formatDateMedium.format(date)
            message2.heure= locale.formatTimeShort.format(date)
            message2.email=muser.email
            message2.name="${user?.prenom} ${user?.nom}"

            ref.setValue(message).addOnCompleteListener {
                val ref1_1 = FirebaseDatabase.getInstance().getReference("/latest_messages/${user?.id}/${user_chat?.id}")
                ref1_1.setValue(message)
            }.addOnCanceledListener {
                Toast.makeText(baseContext,"Message non envoyé",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(baseContext,"Message non envoyé",Toast.LENGTH_SHORT).show()
            }
            ref2.setValue(message2).addOnCompleteListener {
                val ref2_2 = FirebaseDatabase.getInstance().getReference("/latest_messages/${user_chat?.id}/${user?.id}")
                ref2_2.setValue(message2)
                edit_text_message_to_send.text.clear()
            }.addOnCanceledListener {
                Toast.makeText(baseContext,"Message non envoyé",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(baseContext,"Message non envoyé",Toast.LENGTH_SHORT).show()
            }
           // fetchChatDialog()
        }
        //list_chat_c.adapter = adapter


    }

    private fun fetchChatDialog() {

        val ref = FirebaseDatabase.getInstance().getReference("/messages/${user?.id}/${user_chat?.id}")
       // Toast.makeText(baseContext,"/messages/${user?.id}/${user_chat?.id}",Toast.LENGTH_LONG).show()
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                adapter = GroupAdapter<ViewHolder>()
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
                list_chat_c.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val msg = p0.getValue(Messages::class.java)

                //Toast.makeText(baseContext,"ok",Toast.LENGTH_LONG).show()
                if (msg != null){
                    if (msg.getId_env() == user?.id){
                        adapter.add(ChatItemEnv(msg,user!!))
                    }
                    else{
                        adapter.add(ChatItemRec(msg,user_chat))
                    }
                }
                list_chat_c.adapter = adapter
                list_chat_c.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                adapter = GroupAdapter<ViewHolder>()
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
                list_chat_c.scrollToPosition(adapter.itemCount-1)
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(baseContext,"pas ok",Toast.LENGTH_LONG).show()
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
            viewHolder.itemView.textView_pseudo.text = "${msg.email}"
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
            viewHolder.itemView.textView_pseudo.text = "${msg.email}"
            Glide.with(viewHolder.root.context).load(user?.profile).into(viewHolder.itemView.circleImageView_icon_chat_env)
        }

    }

}

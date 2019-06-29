package com.example.digi_move

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import de.hdodenhof.circleimageview.CircleImageView
import khronos.Dates
import khronos.beginningOfDay
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
    private lateinit var adapter : GroupAdapter<ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        adapter = GroupAdapter<ViewHolder>()
        auth = FirebaseAuth.getInstance()
        muser = auth.currentUser!!
        get_user()
        user_chat = intent.getParcelableExtra<Users>(NouveauMessageActivity.USER_CHAT)
        toolbar2.title ="" //"${user_chat.prenom}"
        textView_pseudo_toolbar.text = "${user_chat.prenom} ${user_chat.nom}"
        Glide.with(this).load(user_chat.profile).into(imageView_user_toolbar)


        //val adapter = GroupAdapter<ViewHolder>()


        button_envoyer.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("/messages/${user?.id}/${user_chat?.id}").push()
            Toast.makeText(baseContext,ref.key,Toast.LENGTH_SHORT).show()
            val ref2 = FirebaseDatabase.getInstance().getReference("/messages/${user_chat?.id}/${user?.id}/${ref.key}")
            val date = DateTimeTz.nowLocal()
            val locale = KlockLocale.default
            val message = Messages()
            message.uid = ref.key
            message.id_message = user_chat?.id
            message.id_env = muser?.uid
            message.id_rec = user_chat.id
            message.lu = true
            message.message = edit_text_message_to_send.text.toString()
            message.date= locale.formatDateShort.format(date)
            message.heure= locale.formatTimeShort.format(date)
            message.email=muser.email
            message.name="${user?.prenom} ${user?.nom}"

            val message2 = Messages()
            message2.uid = ref.key
            message2.id_message = muser?.uid
            message2.id_env =  muser?.uid
            message2.id_rec = user_chat.id
            message2.lu = false
            message2.message = edit_text_message_to_send.text.toString()
            message2.date= locale.formatDateShort.format(date)
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
        btn_retour_toolbar.setOnClickListener {
            finish()
        }
        imageView_user_toolbar.setOnClickListener {
            Show_profile(applicationContext)
        }



    }

    private fun Show_profile(ctx : Context) {
        val inflater = layoutInflater
        val container: ViewGroup = findViewById(R.id.layout_profile_info_custom)
        val layout: ViewGroup = inflater.inflate(R.layout.layout_profile_info, container) as ViewGroup
        val username: TextView = layout.findViewById(R.id.textView_username_profile_info)
        val email: TextView = layout.findViewById(R.id.textView_email_profile_info)
        val tel: TextView = layout.findViewById(R.id.textView_tel_profile_info)
        val ville: TextView = layout.findViewById(R.id.textView_ville_profile_info)
        val circleImageView : CircleImageView = layout.findViewById(R.id.imageView_profile_info)
        username.text = "${user_chat.prenom} ${user_chat.nom}"
        email.text = user_chat.email
        //tel.text = user_chat
        ville.text = user_chat.adresse
        circleImageView.background = imageView_user_toolbar.drawable
        with(Toast(ctx)) {
            setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
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
                            //updateMessage(msg)
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
                        updateMessage(msg)
                    }
                }
                list_chat_c.adapter = adapter
                list_chat_c.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
               /* adapter = GroupAdapter<ViewHolder>()
                if(!p0.children.none()) {
                    p0.children.forEach {
                        val msg = it.getValue(Messages::class.java)

                        Toast.makeText(baseContext, "ok", Toast.LENGTH_LONG).show()
                        if (msg != null) {
                            if (msg.getId_env() == user?.id) {
                                adapter.add(ChatItemEnv(msg, user!!))
                            } else {
                                adapter.add(ChatItemRec(msg, user_chat))
                            }
                        }


                    }
                    list_chat_c.adapter = adapter
                    //list_chat_c.scrollToPosition(adapter.itemCount - 1)
                }*/
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(baseContext,"pas ok",Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun updateMessage(msg: Messages) {
        msg.lu=true
        val ref1_1 = FirebaseDatabase.getInstance().getReference("/messages/${user?.id}/${msg.uid}")
        ref1_1.setValue(msg)

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
            //viewHolder.itemView.textView_pseudo.text = "${msg.email}"
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
           // viewHolder.itemView.textView_pseudo.text = "${msg.email}"
            Glide.with(viewHolder.root.context).load(user?.profile).into(viewHolder.itemView.circleImageView_icon_chat_env)
        }

    }

}

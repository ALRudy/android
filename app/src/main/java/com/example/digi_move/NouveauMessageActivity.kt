package com.example.digi_move

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_nouveau_message.*
import kotlinx.android.synthetic.main.user_item_layout.view.*

class NouveauMessageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nouveau_message)
        auth = FirebaseAuth.getInstance()
        val adapter = GroupAdapter<ViewHolder>()



        list_latest_message.adapter = adapter

        fetchUsers()
        imageView_retour_planifier.setOnClickListener {
            finish()
        }
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                val muser = auth.currentUser
                p0.children.forEach {
                    val user = it.getValue(Users::class.java)
                    if (user != null &&  user.id != muser?.uid )adapter.add(UserItem(user!!))

                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatActivity::class.java)

                    intent.putExtra(USER_CHAT,userItem.user)
                    startActivity(intent)
                    finish()
                }
                list_latest_message.adapter = adapter
            }

        })
    }

    companion object{
        var USER_CHAT = "USER_CHAT"
    }
}
class UserItem(val user : Users) : Item<ViewHolder>(){
    override fun getLayout(): Int {

    return R.layout.user_item_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_name_last_message.text = "${user.prenom} ${user.nom}"
        viewHolder.itemView.textView_message_last_message.text = "${user.email}"
        Glide.with(viewHolder.root.context).load(user?.profile).into(viewHolder.itemView.imageView_icon_last_message)

    }

}

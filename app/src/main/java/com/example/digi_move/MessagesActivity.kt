package com.example.digi_move

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.activity_planifier.*

class MessagesActivity : AppCompatActivity() {

    private lateinit var database : FirebaseDatabase
    private lateinit var myRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("regions/")

        textView_retour.setOnClickListener {
            finish()
        }
        textView_nouveau_message.setOnClickListener {
            val intent = Intent(this, NouveauMessageActivity::class.java)
            startActivity(intent)
        }
    }

    fun getMessages(){
        val postListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.forEach {
                   // listregions.add(it.value.toString())
                    //  println("${it.key.toString()} : ${it.value.toString()}")
                }
               // afficherLieux()
               //spinner_depart.adapter = ArrayAdapter<String>(this@PlanifierActivity,android.R.layout.simple_list_item_1,listregions.toList())
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }

        }

        myRef.addValueEventListener(postListener)
    }


}

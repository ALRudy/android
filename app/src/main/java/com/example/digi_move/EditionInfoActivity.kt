package com.example.digi_move

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edition_info.*
import java.util.*


class EditionInfoActivity : AppCompatActivity() {
    var photopic : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edition_info)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users/${user?.uid}")

        myRef.setValue("Hello, World!")
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                Toast.makeText(this@EditionInfoActivity,"Value is: $value",Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
        nv_icon2.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            photopic = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,photopic)
            val bitmapDrawable = BitmapDrawable(bitmap)
            nv_icon2.setImageBitmap(bitmap)
            uploadImage(photopic!!)
        }
    }
    fun uploadImage(uri: Uri){
        println("eto aaaaaaaaaaaaaaaaaaaaaaaaa")
        if (uri != null){
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(uri).addOnSuccessListener {
                println("eto bbbbbbbbbbbbbbbbbb")
                Toast.makeText(this@EditionInfoActivity,"href : ${it.metadata?.path}",Toast.LENGTH_SHORT).show()
                ref.downloadUrl.addOnSuccessListener {
                    Toast.makeText(this@EditionInfoActivity,"href : ${it}",Toast.LENGTH_SHORT).show()
                    println("aaaaaaaaaaaaaaaaaaaaa eto "+it)
                }
            }
        }
    }
}

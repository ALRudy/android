package com.example.digi_move

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import java.util.ArrayList
import java.util.function.Consumer

class PlacesActivity : AppCompatActivity() {

    var type_place = "0,1,1;1,1,0;1,1,1;1,1,1"
    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)
        val list_places = getlistplace(type_place)
        list_places.forEach(Consumer<List<String>> { t ->
            println(t.size)
            println("${t.get(0)} ${t.get(1)} ${t.get(2)}")
        })
    }

    private fun getlistplace(type_place: String): MutableList<List<String>> {

        val lignes = type_place.split(';')
        var list = listOf<List<String>>().toMutableList()
        for (l in lignes){
            //println(l)
            list.add(l.split(','))
            //println(list.toString())
        }
        return  list
    }
    class PlaceItemDispo(val msg : Messages) : Item<ViewHolder>() {
        var user_chat: Users? = null
        override fun getLayout(): Int {
            return R.layout.layout_place_voit_dispo
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val ref = FirebaseDatabase.getInstance().getReference("users/${msg.id_rec}")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    user_chat = p0.getValue(Users::class.java)
                }

            })

        }
    }
    class PlaceItemNoUsed(val msg : Messages) : Item<ViewHolder>() {
        var user_chat: Users? = null
        override fun getLayout(): Int {
            return R.layout.layout_place_voit_vide
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

        }
    }
}

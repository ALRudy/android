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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import java.util.ArrayList
import java.util.function.Consumer

class PlacesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var user : Users? = null
    var type_place = "0,1,1;1,1,0;1,1,1;1,1,1"
    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
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
    class Ranger(val str : String,val user : String ) : Item<ViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.layout_rangers
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            var list = listOf<List<String>>().toMutableList()
            var lignes = str.split(',')
            var item = GroupAdapter<ViewHolder>()
            for (l in lignes){
                if (l.equals("0")){
                    item.add(PlaceItemNoUsed())
                }
                if (l.equals("1")){
                    item.add(PlaceItemDispo(user))
                }
            }

        }
    }
    class PlaceItemDispo(val user : String) : Item<ViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.layout_place_voit_dispo
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.setOnClickListener {
                Toast.makeText(viewHolder.root.context,"libre pour ${user}", Toast.LENGTH_SHORT).show()
            }

        }
    }
    class PlaceItemNoUsed() : Item<ViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.layout_place_voit_vide
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.setOnClickListener {
                Toast.makeText(viewHolder.root.context,"vide", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

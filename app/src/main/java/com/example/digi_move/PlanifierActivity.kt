package com.example.digi_move

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.get
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_planifier.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class PlanifierActivity : AppCompatActivity() {

    private lateinit  var calendar: Calendar
    private lateinit var database : FirebaseDatabase
    private lateinit var myRef : DatabaseReference
    private lateinit var listregions : HashSet<String>
    private var mdepart = ""
    private var mdestination = ""
    private var mdate = ""
    private var mheure = 0
    private var mminute = 0

    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planifier)
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("regions/")

        listregions = HashSet()
        calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        imageView_retour_planifier.setOnClickListener {
            finish()
        }

        btn_date_pick.setOnClickListener {

            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view: DatePicker, mYear: Int, mMonth, mDayOfMonth ->
                    btn_date_pick.text = "$mDayOfMonth/$mMonth/$mYear"
                    mdate = "$mDayOfMonth/$mMonth/$mYear"
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        btn_time_pick.setOnClickListener {
            val tmp = TimePickerDialog.OnTimeSetListener { view: TimePicker, mHourOfDay, mMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, mHourOfDay)
                calendar.set(Calendar.MINUTE, mMinute)
                btn_time_pick.text = SimpleDateFormat("HH:mm").format(calendar.time)
            }
            TimePickerDialog(this, tmp, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
        //setValListLieux()
        getLieux()



        spinner_depart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mdepart = listregions.elementAt(position)
                Toast.makeText(this@PlanifierActivity, mdepart, Toast.LENGTH_SHORT).show()
                val listRegionDest = listregions.filter {
                    it != mdepart
                }
                spinner_destination.adapter = ArrayAdapter<String>(this@PlanifierActivity,android.R.layout.simple_list_item_1,listRegionDest.toList())
            }

        }
        spinner_destination.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mdepart = listregions.elementAt(position)
                Toast.makeText(this@PlanifierActivity, mdepart, Toast.LENGTH_SHORT).show()
            }
        }

        id_btn_places.setOnClickListener {
            val intent = Intent(this, PlacesActivity::class.java)
            startActivity(intent)
        }
    }
    fun getLieux(){
        val postListener = object : ValueEventListener  {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.forEach {
                    listregions.add(it.value.toString())
                  //  println("${it.key.toString()} : ${it.value.toString()}")
                }
                afficherLieux()
                spinner_depart.adapter = ArrayAdapter<String>(this@PlanifierActivity,android.R.layout.simple_list_item_1,listregions.toList())
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }

        }

        myRef.addValueEventListener(postListener)
    }
    fun afficherLieux(){
        for (item in listregions){
            Toast.makeText(this@PlanifierActivity, item,Toast.LENGTH_SHORT).show()
            println(item)
        }
    }
    fun setValListLieux(){
        myRef.push().setValue("TANA")
        myRef.push().setValue("DIEGO")
        myRef.push().setValue("NOSY BE")
        myRef.push().setValue("TOAMASINA")
    }
}

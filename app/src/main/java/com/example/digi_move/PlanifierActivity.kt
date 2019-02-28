package com.example.digi_move

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_planifier.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PlanifierActivity : AppCompatActivity() {

    lateinit var calendar: Calendar
    lateinit var database : FirebaseDatabase
    lateinit var myRef : DatabaseReference
    lateinit var  listLieux : MutableMap<String,String>


    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planifier)

        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("regions/")

        listLieux = HashMap<String,String>()
        calendar = Calendar.getInstance()
        var day = calendar.get(Calendar.DAY_OF_MONTH)
        var month =  calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)

        btn_date_pick.setOnClickListener {

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view:DatePicker, mYear:Int, mMonth, mDayOfMonth ->
                btn_date_pick.text = "$mDayOfMonth/$mMonth/$mYear"
            },year,month,day)
            dpd.show()
        }
        btn_time_pick.setOnClickListener {
            val tmp = TimePickerDialog.OnTimeSetListener { view:TimePicker, mHourOfDay, mMinute ->
                calendar.set(Calendar.HOUR_OF_DAY,mHourOfDay)
                calendar.set(Calendar.MINUTE,mMinute)
                btn_time_pick.text=SimpleDateFormat("HH:mm").format(calendar.time)
            }
            TimePickerDialog(this,tmp,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show()
        }
        setValListLieux()
        getLieux()
    }
    fun getLieux(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                //listLieux = dataSnapshot.getValue(MutableMap<String, String>.class)
                /*listLieux.forEach {
                    Toast.makeText(this@PlanifierActivity,it.toString(),Toast.LENGTH_LONG).show()
                }
                post!!.forEachIndexed { index, c ->
                    listLieux.add(c.toString())
                }*/
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message

                // ...
            }
        }

        myRef.addValueEventListener(postListener)
    }
    fun setValListLieux(){
        myRef.push().setValue("TANA")
        myRef.push().setValue("DIEGO")
        myRef.push().setValue("NOSY BE")
        myRef.push().setValue("TOAMASINA")
    }
}

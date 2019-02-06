package com.example.digi_move

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_accueil.*
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.app_bar_principal.*
import kotlinx.android.synthetic.main.nav_header_principal.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PrincipalActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    var user : Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        onStartcheck()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        get_user(this)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }


    fun get_user(context : Context){
        val muser = auth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("users/${muser?.uid}")

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(Users::class.java) as Users?
                txt_pseudo.text = "${user?.pseudo}"
                txt_mail.text = "${user?.email}"
                if (user?.profile != null || user?.profile != ""){
                    Glide.with(baseContext).load(user?.profile).into(imageView)
                }



            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        ref.addListenerForSingleValueEvent(userListener)




    }
    fun logout(){
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                finish()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

    }
    fun delete_account(){
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // ...
            }
    }
    public fun onStartcheck() {

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
        if (currentUser == null) {
            logout()
        }
        if (currentUser != null) {
            if(!currentUser.isEmailVerified) {

                currentUser.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            validation_dialog(currentUser)
                        }
                    }
            }
        }
    }
    private fun validation_dialog(user : FirebaseUser){
        val builder = AlertDialog.Builder(this@PrincipalActivity)

        builder.setCancelable(false)
        builder.setTitle("Validation du mail")
        builder.setMessage("cette adresse n'a pas encore été vérifié, confirmez votre compte en vérifiant votre mail ")
        builder.setPositiveButton("OK"){dialog, which ->
            logout()
        }


        // Display a negative button on alert dialog
        /*	builder.setNegativeButton("Vérifier"){dialog,which ->
                Toast.makeText(applicationContext,"You are not agree.",Toast.LENGTH_SHORT).show()
            }*/


        // Display a neutral button on alert dialog
        builder.setNeutralButton("Annuler"){_,_ ->
            logout()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.itm_Reglage -> {
                // Handle the camera action
            }
            R.id.itm_Groupes -> {

            }
            R.id.itm_Organiser -> {

            }
            R.id.itm_Messages -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }R.id.itm_deconnexion -> {
            logout()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}

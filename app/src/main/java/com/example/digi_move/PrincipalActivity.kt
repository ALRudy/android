package com.example.digi_move

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.activity_scroll.*
import kotlinx.android.synthetic.main.app_bar_principal.*
import kotlinx.android.synthetic.main.content_principal.*
import kotlinx.android.synthetic.main.nav_header_principal.*

class PrincipalActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener{
    private lateinit var auth: FirebaseAuth
    var user : Users? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        onStartcheck()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        get_user(this)
        setSupportActionBar(toolbar)

        var item = GroupAdapter<ViewHolder>()
        item.add(Orga_Class())
        item.add(Orga_Class())
        item.add(Orga_Class())
        item.add(Orga_Class())
        list_accueil.adapter = item
        var i = 0
        icon_messages.setOnClickListener {
            badge_messages.animationDuration = 1
            badge_messages.setNumber(++i)
        }
        var j = 0
        icon_notifications.setOnClickListener {
            badge_notifications.animationDuration = 1
            badge_notifications.setNumber(++j)
        }

      /*  fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/

        btn_planifier_bottom.setOnClickListener {
            val intent = Intent(this, PlanifierActivity::class.java)
            startActivity(intent)
        }


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        /*crdHeaderView.post(Runnable {
            // parameters are (View headerView, int marginTop)
            concealerNSV.setHeaderView(crdHeaderView, 15)
        })*/
        linFooterView.post(Runnable {
            // parameters are (View footerView, int marginBottom)
            concealerNSV.setFooterView(linFooterView, 0)
        })
        icon_messages.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

    }

    fun get_user(context : Context){
        val muser = auth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("users/${muser?.uid}")

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(Users::class.java)
                txt_pseudo.text = "${user?.prenom} ${user?.nom}"
                txt_mail.text = "${user?.email}"
                if (user?.profile != null || user?.profile != ""){
                    Glide.with(baseContext).load(user?.profile).into(imageView)
                    //BlurImage.with(applicationContext).load(imageView.toBitmap(Bitmap.Config.ARGB_8888)).intensity(20.toFloat()).Async(true).into(header_nav.background)
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
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){

            R.id.itm_Reglage -> {
                // Handle the camera action
                Toast.makeText(applicationContext,"ato",Toast.LENGTH_LONG ).show()
                val intent = Intent(applicationContext,ScrollActivity::class.java)
                startActivity(intent)
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

            }
            R.id.itm_deconnexion -> {
                Toast.makeText(applicationContext,"ato koa",Toast.LENGTH_LONG ).show()
                logout()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}

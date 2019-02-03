package com.example.digi_move

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_accueil.*
import kotlinx.coroutines.*

class accueil : AppCompatActivity() {

	private lateinit var auth: FirebaseAuth
	var user : Users? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		auth = FirebaseAuth.getInstance()
		onStartcheck()
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_accueil)
		get_user(this)
		//val user = auth.currentUser

		btn_deconnexion.setOnClickListener {
			logout()
		}
	}


	override fun onBackPressed() {
		finish()
	}


	fun get_user(context : Context){
		val muser = auth.currentUser
		val ref = FirebaseDatabase.getInstance().getReference("users/${muser?.uid}")

		val userListener = object : ValueEventListener {
			override fun onDataChange(dataSnapshot: DataSnapshot) {
				// Get Post object and use the values to update the UI
				user = dataSnapshot.getValue(Users::class.java) as Users?
				textView_bienvenu.text = "${user?.nom + user?.prenom}"
				if (user?.profile != null || user?.profile != ""){
					Glide.with(baseContext).load(user?.profile).into(nv_icon)
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
        val builder = AlertDialog.Builder(this@accueil)

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
            finish()
			val intent = Intent(this, LoginActivity::class.java)
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			startActivity(intent)
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

}

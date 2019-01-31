package com.example.digi_move

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

class accueil : AppCompatActivity() {

	private lateinit var auth: FirebaseAuth
	var user : Users? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_accueil)
		auth = FirebaseAuth.getInstance()
		onStartcheck()
		get_user()
		//val user = auth.currentUser

		btn_deconnexion.setOnClickListener {
			logout()
		}
	}

	override fun onStart() {
		super.onStart()

		updateUI()
	}
	fun updateUI(){
		textView_bienvenu.text = "Salut ${user?.pseudo}"
		Glide.with(this).load(user?.profile).into(nv_icon)
		Toast.makeText(
			baseContext,
			"UI updated",
			Toast.LENGTH_LONG
		).show()
	}
	fun get_user(){
		val muser = FirebaseAuth.getInstance().currentUser
		val ref = FirebaseDatabase.getInstance().getReference("users/${muser?.uid}")
		val userListener = object : ValueEventListener {
			override fun onDataChange(dataSnapshot: DataSnapshot) {
				// Get Post object and use the values to update the UI
				user = dataSnapshot.getValue(Users::class.java)
				Toast.makeText(
					baseContext,
					user?.pseudo.toString(),
					Toast.LENGTH_LONG
				).show()
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
		super.onStart()
		// Check if user is signed in (non-null) and update UI accordingly.
		val currentUser = auth.currentUser
		//updateUI(currentUser)
		if (currentUser == null) {
			finish()
			val intent = Intent(this, LoginActivity::class.java)
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			startActivity(intent)
		}
        if (currentUser != null) {
            if(!currentUser.isEmailVerified) {
                validation_dialog(currentUser)
            }
        }
	}
    private fun validation_dialog(user : FirebaseUser){
        val builder = AlertDialog.Builder(this@accueil)

        builder.setCancelable(false)
        builder.setTitle("Validation du mail")
        builder.setMessage("cette adresse n'a pas encore été vérifier, voulez-vous valider ?")
        builder.setPositiveButton("Oui"){dialog, which ->
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "verifier votre mail pour valider votre compte",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                }
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

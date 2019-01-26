package com.example.digi_move

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_accueil.*

class accueil : AppCompatActivity() {

	private lateinit var auth: FirebaseAuth

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_accueil)
		auth = FirebaseAuth.getInstance()
		val user = auth.currentUser
		textView_bienvenu.text = "Salut ${user?.displayName}"
		//Toast.makeText(baseContext, "${user?.email}  ${user?.uid}",
		//	Toast.LENGTH_LONG).show()
		 Glide.with(this).load(user?.photoUrl).into(nv_icon)
		btn_deconnexion.setOnClickListener {
			logout()
		}
	}
	fun logout(){
		AuthUI.getInstance()
			.signOut(this)
			.addOnCompleteListener {
				finish()
				val intent = Intent(this, MainActivity::class.java)
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
	public override fun onStart() {
		super.onStart()
		// Check if user is signed in (non-null) and update UI accordingly.
		val currentUser = auth.currentUser
		//updateUI(currentUser)
		if (currentUser == null) {
			finish()
			val intent = Intent(this, MainActivity::class.java)
			startActivity(intent)
		}
	}
}

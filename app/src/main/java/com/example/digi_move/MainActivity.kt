package com.example.digi_move

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
	private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
	    // Initialize Firebase Auth
	    hideProgressDialog()
	    auth = FirebaseAuth.getInstance()
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        btn_signin.setOnClickListener(){

            val email = editText_email.text.toString()
            val mdp = editText_mdp.text.toString()

            if (email.isEmpty()or mdp.isEmpty())return@setOnClickListener

            //Toast.makeText(this,"${email} ${mdp}",Toast.LENGTH_SHORT).show()

	        signin_account(email,mdp)

        }

	    btn_signin_social_networl.setOnClickListener {
		    val intent = Intent(this, LoginActivity::class.java)
		    startActivity(intent)
	    }

    }

    fun signin_account(email: String, mdp: String) {
	    auth.createUserWithEmailAndPassword(email, mdp)
		    .addOnCompleteListener {
			    if (it.isSuccessful) {
					showProgressDialog()
				    finish()
				    val intent = Intent(this, accueil::class.java)
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				    startActivity(intent)
			    } else {
				    // If sign in fails, display a message to the user.
					try{
						throw it.exception!!
					}
					catch (weakPassword : FirebaseAuthWeakPasswordException){
						Toast.makeText(this,"veuillez taper un mot de passe plus robuste à plus de 6 caracteres",Toast.LENGTH_SHORT).show()
					}
					catch (existmail : FirebaseAuthUserCollisionException ){
						Toast.makeText(this,"cette adresse est déjà utilisée",Toast.LENGTH_SHORT).show()
					}
					catch (malformed : FirebaseAuthInvalidCredentialsException){
						Toast.makeText(this,"cette adresse mal formée",Toast.LENGTH_SHORT).show()
					}
			    }

			    // ...
		    }

    }

	public override fun onStart() {
		super.onStart()
		// Check if user is signed in (non-null) and update UI accordingly.
		val currentUser = auth.currentUser
		//updateUI(currentUser)
		if (currentUser != null){
			val intent = Intent(this, accueil::class.java)
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			startActivity(intent)
			finish()
		}
	}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
	fun showProgressDialog() {
		progressBar.isIndeterminate = true
		progressBar.visibility = View.VISIBLE
	}

	fun hideProgressDialog() {

		progressBar.isIndeterminate = false
		progressBar.visibility = View.GONE
	}
}

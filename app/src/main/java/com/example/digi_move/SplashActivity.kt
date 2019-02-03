package com.example.digi_move

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = FirebaseAuth.getInstance()
        Handler().postDelayed({
            val currentUser = auth.currentUser
            //updateUI(currentUser)
            if (currentUser == null) {
                val option = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(img_logo,"logo_img"))
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            if (currentUser != null) {
                if(currentUser.isEmailVerified) {
                    val option = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(img_logo,"logo_img"))
                    val intent = Intent(this, accueil::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                else{
                    val option = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(img_logo,"logo_img"))
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }
        },3000)
    }
}

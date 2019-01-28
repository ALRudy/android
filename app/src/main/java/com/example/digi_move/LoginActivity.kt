package com.example.digi_move

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.Manifest.permission.READ_CONTACTS
import android.content.*
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseUiException
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_main.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private var mAuthTask: UserLoginTask? = null
	private lateinit var auth: FirebaseAuth
	private  var googleSignInClient : GoogleSignInClient?=null
	private var callbackManager : CallbackManager?=null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		// Set up the login form.
		auth = FirebaseAuth.getInstance()
		FacebookSdk.sdkInitialize(applicationContext)

		callbackManager = CallbackManager.Factory.create()

		btn_facebook_signin.setReadPermissions("email","public_profile")

		btn_facebook_signin.setOnClickListener {
			signIn_facebook()
		}

		populateAutoComplete()
		password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
			if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
				attemptLogin()
				return@OnEditorActionListener true
			}
			false
		})

		email_sign_in_button.setOnClickListener { attemptLogin() }
		// Configure Google Sign In
		val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(getString(R.string.default_web_client_id))
			.requestEmail()
			.build()
		googleSignInClient = GoogleSignIn.getClient(this,gso)

		btn_google_signin.setOnClickListener {
			signIn_google()
		}
		txt_mdp_oublie.setOnClickListener {
			//Snackbar.make(it.rootView, "appuyer ici pour ne rien faire", Snackbar.LENGTH_LONG)
			//	.setAction("ICI", null).show()
			if (!email.text.toString().isEmpty()){
				auth.sendPasswordResetEmail(email.text.toString())
					.addOnCompleteListener { task ->
						if (task.isSuccessful) {
							Toast.makeText(baseContext, "Email envoyé",
								Toast.LENGTH_SHORT).show()
						}
					}
			}
		}
	}

	override fun onBackPressed() {
		AuthUI.getInstance()
			.signOut(this)
			.addOnCompleteListener {
				finish()
			}
		super.onBackPressed()
	}

	private fun signIn_facebook() {

    callbackManager = CallbackManager.Factory.create();

    LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
		override fun onSuccess(loginResult: LoginResult) {
			handleFacebookAccessToken(loginResult.accessToken)
			showProgress(true)

		}

		override fun onCancel() {

			// ...
		}

		override fun onError(error: FacebookException) {

			// ...
		}
	})

	}

	private fun handleFacebookAccessToken(token: AccessToken) {

		val credential = FacebookAuthProvider.getCredential(token.token)
		auth.signInWithCredential(credential)
			.addOnCompleteListener(this) { task ->
				if (task.isSuccessful) {

					finish()
					val intent = Intent(this, accueil::class.java)
					startActivity(intent)
				} else {
					// If sign in fails, display a message to the user.

					Toast.makeText(baseContext, "Authentication failed.",
						Toast.LENGTH_SHORT).show()
				}

				// ...
			}
	}

	private fun signIn_google() {
		val signInIntent = googleSignInClient?.signInIntent
		startActivityForResult(signInIntent, 101)
	}

	public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		// Pass the activity result back to the Facebook SDK
		callbackManager?.onActivityResult(requestCode, resultCode, data)


		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == 101) {
			val task = GoogleSignIn.getSignedInAccountFromIntent(data)
			try {
				// Google Sign In was successful, authenticate with Firebase
				val account = task.getResult(ApiException::class.java)
				firebaseAuthWithGoogle(account!!)
			} catch (e: ApiException) {
				// Google Sign In failed, update UI appropriately

				// ...
			}
		}
	}

	private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

		val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
		auth.signInWithCredential(credential)
			.addOnCompleteListener(this) { task ->
				if (task.isSuccessful) {

					showProgress(true)
					finish()
					val intent = Intent(this, accueil::class.java)
					startActivity(intent)
				} else {
					// If sign in fails, display a message to the user.
					//updateUI(null)
				}

				// ...
			}
	}

	private fun populateAutoComplete() {
		if (!mayRequestContacts()) {
			return
		}

		loaderManager.initLoader(0, null, this)
	}

	private fun mayRequestContacts(): Boolean {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true
		}
		if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			return true
		}
		if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
			Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
				.setAction(android.R.string.ok,
					{ requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) })
		} else {
			requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
		}
		return false
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	override fun onRequestPermissionsResult(
		requestCode: Int, permissions: Array<String>,
		grantResults: IntArray
	) {
		if (requestCode == REQUEST_READ_CONTACTS) {
			if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				populateAutoComplete()
			}
		}
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private fun attemptLogin() {
		if (mAuthTask != null) {
			return
		}

		// Reset errors.
		email.error = null
		password.error = null

		// Store values at the time of the login attempt.
		val emailStr = email.text.toString()
		val passwordStr = password.text.toString()

		var cancel = false
		var focusView: View? = null

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
			password.error = getString(R.string.error_invalid_password)
			focusView = password
			cancel = true
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(emailStr)) {
			email.error = getString(R.string.error_field_required)
			focusView = email
			cancel = true
		} else if (!isEmailValid(emailStr)) {
			email.error = getString(R.string.error_invalid_email)
			focusView = email
			cancel = true
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView?.requestFocus()
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true)
			signin_account(emailStr, passwordStr)
		}
	}

	fun signin_account(email: String, mdp: String) {
		auth.signInWithEmailAndPassword(email, mdp)
			.addOnCompleteListener {
				if (it.isSuccessful) {
					val user = auth.currentUser
					showProgress(true)
					if (user != null) {
						if(!user.isEmailVerified) {
							validation_dialog(user)
						}
						else{
							finish()
							val intent = Intent(this, accueil::class.java)
							startActivity(intent)
						}
					}

				}
				else {
					try{
						throw it.exception!!
					}
					catch (invalidmail : FirebaseAuthInvalidUserException ){
						Toast.makeText(this,"adresse invalide",Toast.LENGTH_SHORT).show()
						showProgress(false)
					}
					catch (invalidpassword : FirebaseAuthInvalidCredentialsException ){
						Toast.makeText(this,"le mot de passe ne correspond pas",Toast.LENGTH_SHORT).show()
						showProgress(false)
					}
				}

			}
	}


	private fun validation_dialog(user :FirebaseUser){
		val builder = AlertDialog.Builder(this@LoginActivity)

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

						showProgress(false)
					}
				}
		}


		// Display a negative button on alert dialog
	/*	builder.setNegativeButton("Vérifier"){dialog,which ->
			Toast.makeText(applicationContext,"You are not agree.",Toast.LENGTH_SHORT).show()
		}*/


		// Display a neutral button on alert dialog
		builder.setNeutralButton("Annuler"){_,_ ->
			showProgress(false)
		}

		// Finally, make the alert dialog using builder
		val dialog: AlertDialog = builder.create()

		// Display the alert dialog on app interface
		dialog.show()
	}

	private fun isEmailValid(email: String): Boolean {
		//TODO: Replace this with your own logic
		return email.contains("@")
	}

	private fun isPasswordValid(password: String): Boolean {
		//TODO: Replace this with your own logic
		return password.length > 5
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private fun showProgress(show: Boolean) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

			login_form.visibility = if (show) View.GONE else View.VISIBLE
			login_form.animate()
				.setDuration(shortAnimTime)
				.alpha((if (show) 0 else 1).toFloat())
				.setListener(object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator) {
						login_form.visibility = if (show) View.GONE else View.VISIBLE
					}
				})

			login_progress.visibility = if (show) View.VISIBLE else View.GONE
			login_progress.animate()
				.setDuration(shortAnimTime)
				.alpha((if (show) 1 else 0).toFloat())
				.setListener(object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator) {
						login_progress.visibility = if (show) View.VISIBLE else View.GONE
					}
				})
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			login_progress.visibility = if (show) View.VISIBLE else View.GONE
			login_form.visibility = if (show) View.GONE else View.VISIBLE
		}
	}

	override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
		return CursorLoader(
			this,
			// Retrieve data rows for the device user's 'profile' contact.
			Uri.withAppendedPath(
				ContactsContract.Profile.CONTENT_URI,
				ContactsContract.Contacts.Data.CONTENT_DIRECTORY
			), ProfileQuery.PROJECTION,

			// Select only email addresses.
			ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(
				ContactsContract.CommonDataKinds.Email
					.CONTENT_ITEM_TYPE
			),

			// Show primary email addresses first. Note that there won't be
			// a primary email address if the user hasn't specified one.
			ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
		)
	}

	override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
		val emails = ArrayList<String>()
		cursor.moveToFirst()
		while (!cursor.isAfterLast) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS))
			cursor.moveToNext()
		}

		addEmailsToAutoComplete(emails)
	}

	override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

	}

	private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		val adapter = ArrayAdapter(
			this@LoginActivity,
			android.R.layout.simple_dropdown_item_1line, emailAddressCollection
		)

		email.setAdapter(adapter)
	}

	object ProfileQuery {
		val PROJECTION = arrayOf(
			ContactsContract.CommonDataKinds.Email.ADDRESS,
			ContactsContract.CommonDataKinds.Email.IS_PRIMARY
		)
		val ADDRESS = 0
		val IS_PRIMARY = 1
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	inner class UserLoginTask internal constructor(private val context: Context,private val mEmail: String, private val mPassword: String) :
		AsyncTask<Void, Void, Boolean>() {

		override fun doInBackground(vararg params: Void): Boolean? {
			// TODO: attempt authentication against a network service.

			return true //signin_account(mEmail,mPassword)
		}

		override fun onPostExecute(success: Boolean?) {
			mAuthTask = null
			showProgress(false)

			if (success!!) {

			} else {
				password.error = getString(R.string.error_incorrect_password)
				password.requestFocus()
			}
		}

		override fun onCancelled() {
			mAuthTask = null
			showProgress(false)
		}

	}

	companion object {

		/**
		 * Id to identity READ_CONTACTS permission request.
		 */
		private val REQUEST_READ_CONTACTS = 0

		/**
		 * A dummy authentication store containing known user names and passwords.
		 * TODO: remove after connecting to a real authentication system.
		 */
		private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
	}
}

package com.example.digi_move

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancydialoglib.FancyAlertDialog
import com.shashank.sony.fancydialoglib.Icon
import kotlinx.android.synthetic.main.activity_edition_info.*
import libs.mjn.prettydialog.PrettyDialog
import java.util.*
import me.echodev.resizer.Resizer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class EditionInfoActivity : AppCompatActivity() {
    var photopic : Uri? = null
    var profile =""
    private lateinit var auth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var myRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edition_info)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        // Write a message to the database
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("users/${user?.uid}")

        //myRef.setValue("Hello, World!")
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(Users::class.java)
                Toast.makeText(this@EditionInfoActivity,"Value is: $value",Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
        nv_icon3.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 0)
        }
        textView_valider.setOnClickListener {
            saveUser()

            if (user != null) {
                if(!user.isEmailVerified) {
                    validation_dialog(user)
                }
                else{
                    finish()
                    val intent = Intent(baseContext, PrincipalActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK && data != null){
            photopic = data.data
          //  val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,photopic)
            val path = saveImage(photopic!!)
            Toast.makeText(applicationContext,path,Toast.LENGTH_LONG).show()
            println(path)
            val finalFile = File(path)
            /* val compressedImage = Compressor(this)
                 .setQuality(50)
                 .setCompressFormat(Bitmap.CompressFormat.WEBP)
                 .compressToFile(finalFile)*/

             val resizedImage = Resizer(this)
                 .setQuality(50)
                 .setTargetLength(1080)
                 .setSourceImage(finalFile).resizedFile
             pDialog2.show()
             //val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,photopic)
             //val bitmapDrawable = BitmapDrawable(bitmap)
            uploadImage(resizedImage.toUri())
            //nv_icon2.setImageBitmap(bitmap)
        }
    }
    fun saveImage(uri: Uri):String {
        val bytes = ByteArrayOutputStream()
        val bitmaprotate = BitmapRotate()
        val myBitmap = bitmaprotate.HandleSamplingAndRotationBitmap(applicationContext,uri)
        myBitmap?.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
        val wallpaperDirectory = File(
            (Environment.getDownloadCacheDirectory().toString() ))
        // have the object build the directory structure, if needed.
        println(wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            println(wallpaperDirectory.toString())
            val f = File("/storage/emulated/0/Pictures", ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            println("File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }
    fun uploadImage(uri: Uri){
        println("eto aaaaaaaaaaaaaaaaaaaaaaaaa")
        //if (uri != null){

            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(uri).addOnSuccessListener {
                println("eto bbbbbbbbbbbbbbbbbb")
                //Toast.makeText(this@EditionInfoActivity,"href : ${it.metadata?.path}",Toast.LENGTH_SHORT).show()
                ref.downloadUrl.addOnSuccessListener {
                    //Toast.makeText(this@EditionInfoActivity,"href : ${it}",Toast.LENGTH_SHORT).show()
                    profile = it.toString()

                    Glide.with(this).load(photopic).into(nv_icon3)
                    pDialog2.hide()
                }
            }
       // }
    }

    private fun saveUser() {
        val muser = auth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("users/${muser?.uid}")
        val user = Users(muser?.uid,muser?.email,muser?.displayName,editText_nom.text.toString(),
            editText_prenom.text.toString(),editText_adresse.text.toString(), Integer.parseInt(editText_phone.text.toString()),profile)
        user.isFirstLog = false
        ref.setValue(user)
    }
    private fun validation_dialog(user : FirebaseUser){

        FancyAlertDialog.Builder(this)
            .setTitle("Validation du mail")
            .setBackgroundColor(Color.parseColor("#FF040814"))  //Don't pass R.color.colorvalue
            .setMessage("cette adresse n'a pas encore été vérifier, voulez-vous valider ?")
            .setNegativeBtnText("Annuler")
            .setPositiveBtnBackground(Color.parseColor("#FF040814"))  //Don't pass R.color.colorvalue
            .setPositiveBtnText("Oui, valider !")
            .setNegativeBtnBackground(Color.parseColor("#FF040814"))  //Don't pass R.color.colorvalue
            .setAnimation(com.shashank.sony.fancydialoglib.Animation.POP)
            .isCancellable(false)
            .setIcon(R.drawable.ic_danger, Icon.Visible).OnPositiveClicked {
                user.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            sucessDialog("envoi de mail!\nverifier votre mail pour valider votre compte")
                            pDialog2.hide()
                            Thread.sleep(2000)
                            logout()
                        }
                    }
            }
            .OnNegativeClicked {
                pDialog2.hide()
                logout()
            }
            .build()
    }
    fun errorDialog(str : String){
        /*FancyAlertDialog.Builder(this)
            .setTitle("Erreur")
            .setBackgroundColor(Color.parseColor("#FF040814"))  //Don't pass R.color.colorvalue
            .setMessage(str)
            .setNegativeBtnText("Annuler")
            .setPositiveBtnBackground(Color.parseColor("#FF040814"))  //Don't pass R.color.colorvalue
            .setPositiveBtnText("Ok")
            .setNegativeBtnBackground(Color.parseColor("#FF040814"))  //Don't pass R.color.colorvalue
            .setAnimation(com.shashank.sony.fancydialoglib.Animation.POP)
            .isCancellable(false)
            .setIcon(R.drawable.ic_danger,Icon.Visible).OnPositiveClicked {

            }
            .OnNegativeClicked {

            }
            .build()*/
        PrettyDialog(this)
            .setTitleColor(R.color.rouge)
            .setIcon(R.drawable.ic_danger)
            .setMessageColor(R.color.vert_clair)
            .setTitle("Erreur")
            .setMessage(str)
            .show()
    }
    fun sucessDialog(str: String){
        PrettyDialog(this)
            .setTitleColor(R.color.vert_ko)
            .setIcon(R.drawable.ic_checked)
            .setMessageColor(R.color.vert_clair)
            .setTitle("Succès")
            .setMessage(str)
            .show()
    }
    fun logout(){
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                finish()
                val intent = Intent(baseContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

    }
    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
    }
}

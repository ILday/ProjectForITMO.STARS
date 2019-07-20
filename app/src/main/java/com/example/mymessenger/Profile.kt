package com.example.mymessenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.mymessenger.messages.LatestMessageActivity
import com.example.mymessenger.messages.NewMessageActivity
import com.example.mymessenger.models.User
import com.example.mymessenger.views.PasswordDialog
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.view.*
import kotlinx.android.synthetic.main.bottom_navigation_view.*
import java.util.*

class Profile : AppCompatActivity(), PasswordDialog.Listener {

    private val TAG = "ProfileActivity"
    private var photoChange = false
    private var powerNow: Int? = 0
    private var profileImageUrlNow: String = ""
    private var uidNow: String = ""
    private var emailNow: String = ""
    private  lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mUser: User //из бд текущий
    private lateinit var mPendingUser: User // на который хотим поменять
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        data_button_profile.setOnClickListener {
            Log.d("ProfileActivity", "Go to AdminEdit");

            val intent = Intent(this, AdminEdit::class.java)
            startActivity(intent)
        }



        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("users").child(mAuth.currentUser!!.uid).addListenerForSingleValueEvent(object: ValueEventListener{    //берём у бд из users по uid

            override fun onDataChange(data: DataSnapshot) {
                    mUser = data.getValue(User::class.java)!!//конвертируем полученные значения в класс User
                    username_edittext_edit.setText(mUser!!.username, TextView.BufferType.EDITABLE)
                    email_editText_edit.setText(mUser!!.email, TextView.BufferType.EDITABLE)
                    phone_number_editText_edit.setText(mUser!!.phoneNumber, TextView.BufferType.EDITABLE)
                    powerNow = mUser.power
                    profileImageUrlNow = mUser.profileImageUrl
                    uidNow = mUser.uid
                    emailNow = mUser.email
                GlideApp.with(this@Profile)
                    .load(mUser.profileImageUrl)
                    .into(selectphoto_imageview_edit)
                //Picasso.get().load(mUser.profileImageUrl).into(selectphoto_imageview_edit)
                select_photo_button_edit.alpha = 0f
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ", error.toException())
            }

        })
        bottomNavigationViewEx_latest_message.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_messages -> {

                    val intent = Intent(this, LatestMessageActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_search -> {
                    val intent = Intent(this, NewMessageActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {

                    true
                }
                else -> false
            }
        }

        select_photo_button_edit.setOnClickListener {
            Log.d("ProfileActivity", "Try to show photo selector");

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        register_button_edit.setOnClickListener {
            myUpdateProfile()
        }//лямбда-выражения

    }



    var selectedPhotoUri: Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null)
        {   //proceed and check what the selected image was...
            Log.d("ProfileActivity", "Photo was selected");

            selectedPhotoUri = data.data
            //растровое изображение хз
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_edit.setImageBitmap(bitmap)
            select_photo_button_edit.alpha = 0f

            photoChange = true
            uploadImageToFirebaseStorage()

            //    val bitmapDrawable = BitmapDrawable(bitmap)
            //    select_photo_button.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun myUpdateProfile() {

        if (photoChange) {
            Log.d("ProfileActivity", "Update photo")
            mPendingUser = User(
                uid = uidNow,
                username = username_edittext_edit.text.toString(),
                email = email_editText_edit.text.toString(),
                phoneNumber = phone_number_editText_edit.text.toString(),
                power = powerNow,
                profileImageUrl = profileImageUrlNow,
                position = mUser.position,
                department = mUser.department
            )
            ReallyUpdate(mPendingUser)
        } else {
            mPendingUser = User(
                uid = uidNow,
                username = username_edittext_edit.text.toString(),
                email = email_editText_edit.text.toString(),
                phoneNumber = phone_number_editText_edit.text.toString(),
                power = powerNow,
                profileImageUrl = profileImageUrlNow,
                position = mUser.position,
                department = mUser.department
            )
            ReallyUpdate(mPendingUser)
        }
    }

    private fun ReallyUpdate(user: User){
        val error = validate(user)
        if(error == null){
            if(user.email == mUser.email){
                    val updatesMap = mutableMapOf<String, Any>()
                    if(user.username != mUser.username) updatesMap["username"] = user.username
                    if(user.email != mUser.email) updatesMap["email"] = user.email
                    if(user.phoneNumber != mUser.phoneNumber) updatesMap["phoneNumber"] = user.phoneNumber
                    if(user.profileImageUrl!= mUser.profileImageUrl) updatesMap["profileImageUrl"] = user.profileImageUrl
                Log.d("ProfileActivity", "user.profileImageUrl = ${user.profileImageUrl}")
                Log.d("ProfileActivity", "mUser.profileImageUrl = ${mUser.profileImageUrl}")
                Log.d("ProfileActivity", "profileImageUrlNow = $profileImageUrlNow")
                mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap)
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }

            }else{
                PasswordDialog().show(supportFragmentManager, "passord_dialog")
            }
        }else{
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPasswordConfirm(password: String) {
        Log.d("ProfileActivity", "onPasswordConfirm password: $password")
        val credential = EmailAuthProvider.getCredential(mUser.email, password)
        mAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener{
            if(it.isSuccessful){
                mAuth.currentUser!!.updateEmail(mPendingUser.email).addOnCompleteListener {
                    if(it.isSuccessful)
                    {

                        val updatesMap = mutableMapOf<String, Any>()
                        if(mPendingUser.username != mUser.username) updatesMap["username"] = mPendingUser.username
                        if(mPendingUser.email != mUser.email) updatesMap["email"] = mPendingUser.email
                        if(mPendingUser.phoneNumber != mUser.phoneNumber) updatesMap["phoneNumber"] = mPendingUser.phoneNumber
                        if(mPendingUser.profileImageUrl!= mUser.profileImageUrl) updatesMap["profileImageUri"] = mPendingUser.profileImageUrl

                        mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap)
                            .addOnCompleteListener{
                                if(it.isSuccessful){
                                    Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
                                    finish()
                                }else{
                                    Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        //ReallyUpdate(mPendingUser)
                    }else{
                        Toast.makeText(this, it.exception!!.message!!, Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, it.exception!!.message!!, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validate(user: User): String? =
        when{
            user.username.isEmpty() -> "Please enter your username"
            user.email.isEmpty() -> "Please enter your email"
            user.phoneNumber.isEmpty() -> "Please enter your phone number"
            else -> null
        }


     //   val user = User()

    /*     val email = email_editText_edit.text.toString();
         val password = password_editText_edit.text.toString();

         if(email.isEmpty() || password.isEmpty()) {
             Toast.makeText(this, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
             return//@setOnClickListener
         }

         Log.d("ProfileActivity", "Email is: " + email);    //LogCat идём в Debug и в поиске ищем RegisterActivity
         Log.d("ProfileActivity", "Password is: $password");


     /*   val userUpdate = FirebaseAuth.getInstance().currentUser

        userUpdate?.updateEmail(email_editText_edit.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ProfileActivity", "User email address updated.")
                }
            }
*/
/*
         FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
             .addOnCompleteListener {
                 if(!it.isSuccessful) return@addOnCompleteListener

                 Log.d("ProfileActivity", "Successfully created user with uid: ${it.result.user.uid}")

                 uploadImageToFirebaseStorage()
             }
             .addOnFailureListener{
                 Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                 Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
             }*/
*/

    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename") //ссылка на хранилище бд

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("ProfileActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener{
                    Log.d("ProfileActiviry", "File location: $it")
                profileImageUrlNow = it.toString()
              //      saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        Log.d("ProfileActivity", "1")
        val uid = FirebaseAuth.getInstance().uid ?:""
        Log.d("ProfileActivity", "2")
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        Log.d("ProfileActivity", "3")

        val user = User(uid, username_edittext_edit.text.toString(), email_editText_edit.text.toString(), phone_number_editText_edit.text.toString(),0, profileImageUrl, mUser.position, mUser.department)
        Log.d("ProfileActivity", "4")


        //val profileUpdates = UserProfileChangeRequest.Builder()

            //    .setDisplayName("Jane Q. User")
        //    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
            //.build()



  /*      ref.setValue(user)
            .addOnSuccessListener {
                Log.d("ProfileActivity", "Finally we edit the user in Firebase Database")

                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) //флаг очищает стек?!
                startActivity(intent)

            }
            .addOnFailureListener{
                Log.d("ProfileActivity", "Failed Firebase Database: ${it.message}")
            }
*/
    }
}


package com.example.mymessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.icu.util.UniversalTimeScale.toLong
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.mymessenger.R
import com.example.mymessenger.messages.LatestMessageActivity
import com.example.mymessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.util.*

class RegisterActivity : AppCompatActivity(), KeyboardVisibilityEventListener {
    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if(isKeyboardOpen){
            scrollview_register.scrollTo(0,scrollview_register.bottom)
        }else{
            scrollview_register.scrollTo(0,scrollview_register.top)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        KeyboardVisibilityEvent.setEventListener(this, this)

        val email = email_editText_register.text.toString();
        val password = password_editText_register.text.toString();

        Log.d("RegisterActivity", "Email is: " + email);    //LogCat идём в Debug и в поиске ищем RegisterActivity
        Log.d("RegisterActivity", "Password is: $password");

        select_photo_button.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector");

            intent.type = "image/*"
            val intent = Intent(Intent.ACTION_PICK)
            startActivityForResult(intent, 0)
        }

        register_button_register.setOnClickListener {
           performRegister()
        }//лямбда-выражения

        already_have_account_text_view.setOnClickListener {
            Log.d("RegisterActivity", "Try to show login activity");

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent);
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null)
        {   //proceed and check what the selected image was...
            Log.d("RegisterActivity", "Photo was selected");

            selectedPhotoUri = data.data
                                        //растровое изображение хз
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview.setImageBitmap(bitmap)
            select_photo_button.alpha = 0f


        //    val bitmapDrawable = BitmapDrawable(bitmap)
        //    select_photo_button.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister()
    {
        val email = email_editText_register.text.toString();
        val password = password_editText_register.text.toString();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return//@setOnClickListener
        }

        Log.d("RegisterActivity", "Email is: " + email);    //LogCat идём в Debug и в поиске ищем RegisterActivity
        Log.d("RegisterActivity", "Password is: $password");

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("RegisterActivity", "Successfully created user with uid: ${it.result.user.uid}")

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename") //ссылка на хранилище бд

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener{
                    Log.d("RegisterActiviry", "File location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        Log.d("RegisterActivity", "1")
        val uid = FirebaseAuth.getInstance().uid ?:""
        Log.d("RegisterActivity", "2")
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        Log.d("RegisterActivity", "3")

        val user = User(uid, username_edittext_register.text.toString(), email_editText_register.text.toString(), phone_number_editText_register.text.toString(),0, profileImageUrl,"","")
        Log.d("RegisterActivity", "4")
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved the user to Firebase Database")

                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) //флаг очищает стек?!
                startActivity(intent)

            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed Firebase Database: ${it.message}")
                }

    }

}


package com.example.mymessenger.messages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import com.example.mymessenger.GlideApp
import com.example.mymessenger.R
import com.example.mymessenger.models.ChatMessage
import com.example.mymessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.image_from_row.view.*
import kotlinx.android.synthetic.main.image_to_row.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chatlog.adapter = adapter //позволяет добавить на объект внутри этого адаптера

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username


        val uri = toUser!!.profileImageUrl
        val targetImageView = imageview_chat_log
        //Picasso.get().load(uri).into(targetImageView)

        GlideApp.with(targetImageView)
            .load(uri)
            .into(targetImageView)

        username_textView_chat_log.setText(toUser!!.username, TextView.BufferType.EDITABLE)
       // setupDummyData()
        listenForMessages()

            send_button_chatlog.setOnClickListener {
            Log.d(TAG, "Attempt to send message...")
            performSendMessage()
        }

        image_button_chatlog.setOnClickListener {
            Log.d(TAG, "Photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
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

            if (selectedPhotoUri == null) return

            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename") //ссылка на хранилище бд

            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener{
                        Log.d(TAG, "File location: $it")

                        saveImageMessageToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener{

                }
          //  selectphoto_imageview.setImageBitmap(bitmap)
          //  select_photo_button.alpha = 0f


            //    val bitmapDrawable = BitmapDrawable(bitmap)
            //    select_photo_button.setBackgroundDrawable(bitmapDrawable)
        }
    }


    private fun saveImageMessageToFirebaseDatabase(profileImageUrl: String){

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId =user.uid

        if(fromId == null) return

        //    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()


        val chatMessage = ChatMessage(reference.key!!, profileImageUrl, fromId, toId, System.currentTimeMillis() / 1000, Calendar.getInstance().time.toString(), "image")
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                Log.d(TAG, "time: ${chatMessage.time}")
              //  editText_chatlog.text?.clear()
                recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)

        val chatMessage2 = ChatMessage(reference.key!!, "Picture", fromId, toId, System.currentTimeMillis() / 1000, Calendar.getInstance().time.toString(), "image")


        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage2)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage2)

    }

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
                                                    //прослушиваем сообщения
        ref.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(chatMessage != null){
                    Log.d(TAG, chatMessage.text)

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessageActivity.currentUser
                        if(chatMessage.type == "text")
                            adapter.add(ChatFromItem(chatMessage.text, currentUser!!, chatMessage.time))
                        else
                            adapter.add(ChatImageFromItem(chatMessage.text, currentUser!!, chatMessage.time))

                    }
                    else
                    {
                        if(chatMessage.type == "text")
                            adapter.add(ChatToItem(chatMessage.text, toUser!!, chatMessage.time))
                        else
                            adapter.add(ChatImageToItem(chatMessage.text, toUser!!, chatMessage.time))

                    }
                }
                recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
            }


            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }



    private fun performSendMessage(){
        //how do we actually send a message to firebase
        val text = editText_chatlog.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId =user.uid

        if(fromId == null) return

    //    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()


        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000, Calendar.getInstance().time.toString(), "text")
        reference.setValue(chatMessage)
            .addOnSuccessListener {
            Log.d(TAG, "Saved our chat message: ${reference.key}")
                Log.d(TAG, "time: ${chatMessage.time}")
                        editText_chatlog.text?.clear()
                recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
        }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }


 /*   private fun setupDummyData(){
        val adapter = GroupAdapter<ViewHolder>()

      /*  adapter.add(ChatFromItem("FROM MESSAGE"))
        adapter.add(ChatToItem("TO MESSAGE\nTO MESSAGE"))
        adapter.add(ChatFromItem("FROM MESSAGE"))
        adapter.add(ChatToItem("TO MESSAGE\nTO MESSAGE"))*/

        recyclerview_chatlog.adapter = adapter
    }*/
}

class ChatFromItem(val text: String, val user: User, val date: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
        viewHolder.itemView.textView_message_time_from.text = setTimeText(Date(date))
        //загружает картинку пользователя
    //    val uri = user.profileImageUrl
    //    val targetImageView = viewHolder.itemView.imageview_chat_from_row
    //    Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    private fun setTimeText(date: Date): String{
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        val str = dateFormat.format(date)
        return str
    }
}

class ChatImageFromItem(val url: String, val user: User, val date: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val targetImageView = viewHolder.itemView.image_view_from_row
        Picasso.get().load(url).into(targetImageView)
        viewHolder.itemView.textView_image_message_time_from.text = setTimeText(Date(date))
        //загружает картинку пользователя
        //    val uri = user.profileImageUrl
        //    val targetImageView = viewHolder.itemView.imageview_chat_from_row
        //    Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int {
        return R.layout.image_from_row
    }

    private fun setTimeText(date: Date): String{
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        val str = dateFormat.format(date)
        return str
    }
}


class ChatToItem(val text: String, val user: User, val date: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        viewHolder.itemView.textView_message_time_to.text = setTimeText(Date(date))

        Log.d(ChatLogActivity.TAG, "Тут2")

        //загружает картинку пользователя
    //    val uri = user.profileImageUrl
    //    val targetImageView = viewHolder.itemView.imageview_chat_to_row
    //    Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int {
        Log.d(ChatLogActivity.TAG, "Тут3")

        return R.layout.chat_to_row
    }

    private fun setTimeText(date: Date): String{
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        val str = dateFormat.format(date)
        return str
    }
}

class ChatImageToItem(val url: String, val user: User, val date: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val targetImageView = viewHolder.itemView.image_view_to_row
        Picasso.get().load(url).into(targetImageView)
        viewHolder.itemView.textView_image_message_time_to.text = setTimeText(Date(date))
        //загружает картинку пользователя
        //    val uri = user.profileImageUrl
        //    val targetImageView = viewHolder.itemView.imageview_chat_from_row
        //    Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int {
        return R.layout.image_to_row
    }

    private fun setTimeText(date: Date): String{
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        val str = dateFormat.format(date)
        return str
    }
}



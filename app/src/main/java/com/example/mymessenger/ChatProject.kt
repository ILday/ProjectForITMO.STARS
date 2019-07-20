package com.example.mymessenger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.mymessenger.messages.LatestMessageActivity
import com.example.mymessenger.models.ChatProjectMessage
import com.example.mymessenger.models.Project
import com.example.mymessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_project.*
import kotlinx.android.synthetic.main.chat_from_row_project.view.*
import kotlinx.android.synthetic.main.chat_to_row_project.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatProject : AppCompatActivity(){

    private lateinit var project: Project
    val adapter = GroupAdapter<ViewHolder>()
    var idThisUser = FirebaseAuth.getInstance().uid
    var idLast : String? = null
    var anotherUser: User? = null


    companion object {
        val USER_KEY = "USER_KEY"
        val TAG = "ChatProject"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_project)

        project = intent.getParcelableExtra<Project>(ListProjects.USER_KEY)

        name_project_textView_chat_project.setText(project.name)

        recyclerview_chat_project.adapter = adapter

        listenForMessages()

        send_button_chat_project.setOnClickListener {
            Log.d(TAG, "Attempt to send message...")
            performSendMessage()
        }

    }

    private fun listenForMessages(){
      //  val fromId = FirebaseAuth.getInstance().uid
      //  val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/project-messages/${project.uid}}")
        //прослушиваем сообщения
        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatProjectMessage = p0.getValue(ChatProjectMessage::class.java)

                if(chatProjectMessage != null){
                    Log.d(TAG, chatProjectMessage.text)

                    if(chatProjectMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessageActivity.currentUser
                        if(idLast == currentUser!!.uid)
                            adapter.add(ChatProjectFromItem(chatProjectMessage.text, currentUser!!, chatProjectMessage.time))
                        else
                            adapter.add(ChatProjectPicFromItem(chatProjectMessage.text, currentUser!!, chatProjectMessage.time))
                        idLast = currentUser.uid
                    }
                    else
                    {



                   /*     val refUser = FirebaseDatabase.getInstance().getReference("/users/${chatProjectMessage.fromId}")
                        //получаем доступк ссылке из Firebase бд?!
                        Log.d(TAG, "id user = ${chatProjectMessage.fromId}")
                        refUser.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                val adapter = GroupAdapter<ViewHolder>()


                                    Log.d(TAG, p0.toString())
                                    val user = p0.getValue(User::class.java)
                                    if(user != null)
                                    {
                                        Log.d(TAG, "user != null")
                                        anotherUser = user
                                    }



                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })*/

                        if(idLast == chatProjectMessage.fromId)
                            adapter.add(ChatProjectToItem(chatProjectMessage.text, chatProjectMessage.fromId, chatProjectMessage.time))
                        else
                            adapter.add(ChatProjectPicToItem(chatProjectMessage.text, chatProjectMessage.fromId, chatProjectMessage.time))

                        idLast = chatProjectMessage.fromId




                    }
                }
                recyclerview_chat_project.scrollToPosition(adapter.itemCount - 1)
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
        val text = editText_chat_project.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
       // val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
       // val toId =user.uid

        if(fromId == null) return

        //    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val reference = FirebaseDatabase.getInstance().getReference("/project-messages/${project.uid}}").push()



        val chatMessage = ChatProjectMessage(reference.key!!, text, fromId, System.currentTimeMillis() / 1000, Calendar.getInstance().time.toString())
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                Log.d(TAG, "time: ${chatMessage.time}")
                editText_chat_project.text?.clear()
                recyclerview_chat_project.scrollToPosition(adapter.itemCount - 1)
            }


    }

}


class ChatProjectFromItem(val text: String, val user: User, val date: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.textview_from_row.text = text
            viewHolder.itemView.textView_message_time_from.text = setTimeText(Date(date))

        //загружает картинку пользователя
        //    val uri = user.profileImageUrl
        //    val targetImageView = viewHolder.itemView.imageview_chat_from_row
        //    Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row_project
    }

    private fun setTimeText(date: Date): String{
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        val str = dateFormat.format(date)
        return str
    }
}


class ChatProjectToItem(val text: String, val iduser: String, val date: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        viewHolder.itemView.textView_message_time_to.text = setTimeText(Date(date))

        Log.d(ChatProject.TAG, "Тут2")

        //загружает картинку пользователя
        //    val uri = user.profileImageUrl
        //    val targetImageView = viewHolder.itemView.imageview_chat_to_row
        //    Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int {
        Log.d(ChatProject.TAG, "Тут3")

        return R.layout.chat_to_row_project
    }

    private fun setTimeText(date: Date): String{
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        val str = dateFormat.format(date)
        return str
    }
}
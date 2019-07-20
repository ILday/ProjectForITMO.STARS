package com.example.mymessenger.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.example.mymessenger.ListProjects
import com.example.mymessenger.Profile
import com.example.mymessenger.R
import com.example.mymessenger.models.ChatMessage
import com.example.mymessenger.models.User
import com.example.mymessenger.registerlogin.RegisterActivity
import com.example.mymessenger.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_latest_message.*
import kotlinx.android.synthetic.main.bottom_navigation_view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*


class LatestMessageActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }
    private val toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)

       // toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        image_button_latest_messege.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        bottomNavigationViewEx_latest_message.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_messages -> {

                    true
                }
                R.id.navigation_search -> {
                    val intent = Intent(this, NewMessageActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_projects -> {
                    val intent = Intent(this, ListProjects::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        /*bottomNavigationViewEx_latest_message.setTextVisibility(false)
        bottomNavigationViewEx_latest_message.enableItemShiftingMode(false)
         bottomNavigationViewEx_latest_message.enableShiftingMode(false)
         bottomNavigationViewEx_latest_message.enableAnimation(false)
         for(i in 0 until bottomNavigationViewEx_latest_message.menu.size()){
             bottomNavigationViewEx_latest_message.setIconTintList(i, null)
         }*/

        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

       //нажатие на айтем
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "Click")
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        //setupDummyRows()

        listenForLatestMessages()

        fetchCurrentUser()

        verifyUserIsLoggedIn()
    }



    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(LatestMessageRow(it, this@LatestMessageActivity))
        }
    }

    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return //оператор Элвиса
                latestMessagesMap[p0.key!!] = chatMessage   //помещаем хэш карту   ключ - это пользователь?
                refreshRecyclerViewMessages()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return //оператор Элвиса
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()

//                adapter.add(LatestMessageRow(chatMessage))
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    val adapter = GroupAdapter<ViewHolder>()


  /*  private fun setupDummyRows(){

        adapter.add(LatestMessageRow())

    }*/

    private fun fetchCurrentUser()      //берем пользователя под которым зашли
    {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.username}.")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null)
        {   //не вошёл пользователь
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sing_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}

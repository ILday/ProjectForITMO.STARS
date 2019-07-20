package com.example.mymessenger.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.sax.StartElementListener
import android.support.v7.widget.DividerItemDecoration
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

import android.util.Log
import com.example.mymessenger.GlideApp
import com.example.mymessenger.Profile
import com.example.mymessenger.R
import com.example.mymessenger.models.User

//import com.example.mymessenger.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottom_navigation_view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

     /*   supportActionBar?.title = "Select User"

        val adapter = GroupAdapter<ViewHolder>()
        //?!adapter.add(UserItem())

        recyclerview_newmessage.adapter*/

        recyclerview_newmessage.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


        fetchUsers()

        bottomNavigationViewEx_latest_message.setSelectedItemId(R.id.navigation_search)

        bottomNavigationViewEx_latest_message.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_messages -> {
                    val intent = Intent(this, LatestMessageActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_search -> {

                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    false
                }
                else -> false
            }
        }

    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        //получаем доступк ссылке из Firebase бд?!
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null)
                    {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener{item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)  //view.context взять контекст
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}



class UserItem(val user: User): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textView_newmessage.text = user.username
        //Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_newmessage)
        GlideApp.with(viewHolder.itemView.imageView_newmessage)
            .load(user.profileImageUrl)
            .into(viewHolder.itemView.imageView_newmessage)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}


/*class CustumAdapter: RecyclerView.Adapter<ViewHolder>{
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
    }
}*/

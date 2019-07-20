package com.example.mymessenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import com.example.mymessenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_list_user_edit.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class ListUserEdit: AppCompatActivity(){

    private val TAG = "AdminEdit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_user_edit)
        recyclerview_list_user_edit.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        fetchUsers()
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
                    Log.d("ListUserEdit", it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null)
                    {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener{item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, EditUserAdmin::class.java)  //view.context взять контекст
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

                recyclerview_list_user_edit.adapter = adapter
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
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_newmessage)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}
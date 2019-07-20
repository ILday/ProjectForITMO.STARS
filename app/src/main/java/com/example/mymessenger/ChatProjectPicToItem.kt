package com.example.mymessenger

import com.example.mymessenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.group_to_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatProjectPicToItem(val text: String, val userId: String, val date: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textview_to_row.text = text
        viewHolder.itemView.textView_message_time_to.text = setTimeText(Date(date))

        //загружает картинку пользователя


        val refUser = FirebaseDatabase.getInstance().getReference("/users/$userId")
        //получаем доступк ссылке из Firebase бд?!
        refUser.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()


                val user = p0.getValue(User::class.java)
                if(user != null)
                {
                    val uri = user.profileImageUrl
                    val targetImageView = viewHolder.itemView.imageview_chat_to_row
                    //Picasso.get().load(uri).into(targetImageView)
                    GlideApp.with(targetImageView)
                        .load(uri)
                        .into(targetImageView)
                }



            }

            override fun onCancelled(p0: DatabaseError) {

            }



        })
    }
    override fun getLayout(): Int {
        return R.layout.group_to_row
    }

    private fun setTimeText(date: Date): String{
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        val str = dateFormat.format(date)
        return str
    }
}

package com.example.mymessenger

import com.example.mymessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.group_from_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatProjectPicFromItem(val text: String, val user: User, val date: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textview_from_row.text = text
        viewHolder.itemView.textView_message_time_from.text = setTimeText(Date(date))

        //загружает картинку пользователя
            val uri = user.profileImageUrl
            val targetImageView = viewHolder.itemView.imageview_chat_from_row
            //Picasso.get().load(uri).into(targetImageView)
        GlideApp.with(targetImageView)
            .load(uri)
            .into(targetImageView)
    }
    override fun getLayout(): Int {
        return R.layout.group_from_row
    }

    private fun setTimeText(date: Date): String{
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        val str = dateFormat.format(date)
        return str
    }
}
package com.example.mymessenger.views

import android.content.Context
import com.bumptech.glide.Glide
import com.example.mymessenger.GlideApp
import com.example.mymessenger.R
import com.example.mymessenger.messages.LatestMessageActivity
import com.example.mymessenger.models.ChatMessage
import com.example.mymessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage, val context: Context): Item<ViewHolder>(){
    var chatPartnerUser: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_textView_latest_message.text = chatMessage.text

        val chatPartnerId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid)
        {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_textView_latest_message.text = chatPartnerUser?.username

                val targetImageVIew = viewHolder.itemView.imageview_latest_message
                //Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageVIew)
                val mainView = viewHolder.itemView.constraint_layout_latest_message_row
                GlideApp.with(targetImageVIew)
                    .load(chatPartnerUser?.profileImageUrl)
                    .into(targetImageVIew)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
}
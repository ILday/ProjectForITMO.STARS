package com.example.mymessenger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.mymessenger.models.Project
import com.example.mymessenger.models.User
import com.example.mymessenger.models.UserProject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_list_users_project.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class ListUsersProject: AppCompatActivity(){

    private val TAG = "ListUsersProject"
    private lateinit var mProject: Project //из бд текущий
    private  lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_users_project)



        mProject = intent.getParcelableExtra<Project>(EditProjectAdmin.USER_KEY)


        fetchUsers()

        listenForUsers()

    }

    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        //получаем доступк ссылке из Firebase бд?!
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d(TAG, it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null)
                    {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener{item, view ->

                    val userItem = item as UserItem
                    val us = userItem.user.uid
                    val uIdProject = mProject.uid

                   var ref = FirebaseDatabase.getInstance().getReference("/project-users/$uIdProject/$us")

                    val userProject = UserProject(us, uIdProject)

                    ref.setValue(userProject)
                        .addOnSuccessListener {
                        Log.d(TAG, "Saved user in project: ${ref.key}")
                    }
                }

                recyclerview_list_user_all.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

      private fun listenForUsers() {

          //     val toId = toUser?.uid
          val uIdProject = mProject.uid

          Log.d(TAG, "t1")




          val refUserProject =
              FirebaseDatabase.getInstance().getReference("project-users/$uIdProject")

          var userProjects = arrayListOf<UserProject>()

          refUserProject.addValueEventListener(object : ValueEventListener {
              override fun onDataChange(dataSnapshot: DataSnapshot) {
                  val adapter2 = GroupAdapter<ViewHolder>()
                  recyclerview_list_user_project.adapter = adapter2
                  for (productSnapshot in dataSnapshot.children) {
                      val userProject = productSnapshot.getValue(UserProject::class.java)
                      userProjects.add(userProject!!)
                      val idUser = userProject.uid
                      val refUser =
                          FirebaseDatabase.getInstance().getReference("users/$idUser")
                      refUser.addListenerForSingleValueEvent(object: ValueEventListener {
                          override fun onDataChange(p0: DataSnapshot) {
                              val user = p0.getValue(User::class.java)

                              adapter2.add(UserItem(user!!))
                          }
                                  override fun onCancelled(p0: DatabaseError) {}
                              })
                              Log.d(TAG, "add userProject = ${userProject!!.uid}")
                  }
              }

              override fun onCancelled(databaseError: DatabaseError) {
                  throw databaseError.toException()
              }



          })
      }

}


package com.example.mymessenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import com.example.mymessenger.models.Project
import com.example.mymessenger.models.UserProject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_list_projects.*

class ListProjects: AppCompatActivity(){

    private val TAG = "ListProject"
    val adapter = GroupAdapter<ViewHolder>()

    companion object {
        val USER_KEY = "USER_KEY"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_projects)

        recyclerview_list_projects.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


        listenForProject()
        // fetchDepartments()
    }

    private fun listenForProject(){

        //     val toId = toUser?.uid

        val iduser = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/projects")

        recyclerview_list_projects.adapter = adapter
        //получаем доступк ссылке из Firebase бд?!
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?){


                    val project = p0.getValue(Project::class.java)
                    if(project != null)
                    {
                        Log.d(TAG, "Get project")
                    //    adapter.add(ProjectItem(project))
                        val uIdProject = project.uid

                        val ref2 = FirebaseDatabase.getInstance().getReference("/project-users/$uIdProject/$iduser")

                        ref2.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {


                                    Log.d(TAG, p0.toString())
                                    Log.d(TAG, "Get userproject")

                                val userProject = p0.getValue(UserProject::class.java)
                                    if(userProject != null)
                                    {
                                        //    adapter.add(ProjectItem(project))
                                        val idProject = userProject.idproject

                                        val ref3 = FirebaseDatabase.getInstance().getReference("/projects/$idProject")

                                        ref3.addListenerForSingleValueEvent(object: ValueEventListener {
                                            override fun onDataChange(p0: DataSnapshot) {


                                                    Log.d(TAG, p0.toString())
                                                    Log.d(TAG, "Get project again")
                                                val projecThis = p0.getValue(Project::class.java)
                                                    if(projecThis  != null)
                                                    {
                                                        adapter.add(ProjectItem(projecThis))
                                                    }

                                            }
                                            override fun onCancelled(p0: DatabaseError) {}
                                        })

                                    }


                                adapter.setOnItemClickListener{item, view ->

                                    val projectItem = item as ProjectItem

                                    val intent = Intent(view.context, WatchProject::class.java)  //view.context взять контекст
                                    intent.putExtra(USER_KEY, projectItem.project) //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                    startActivity(intent)

                                    finish()
                                }


                            }
                            override fun onCancelled(p0: DatabaseError) {}
                        })

                    }



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
}
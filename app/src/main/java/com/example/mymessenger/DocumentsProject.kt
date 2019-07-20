package com.example.mymessenger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import com.example.mymessenger.models.Document
import com.example.mymessenger.models.Project
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_documents_project.*
import kotlinx.android.synthetic.main.project_row_edit_project.view.*

class DocumentsProject: AppCompatActivity() {

    var TAG = "DocumentsProject"
    private lateinit var project: Project
    val adapter = GroupAdapter<ViewHolder>()

    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents_project)

        project = intent.getParcelableExtra<Project>(ListProjects.USER_KEY)

        recyclerview_list_documets_project.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        listenForDocument()
    }

    private fun listenForDocument(){

        //     val toId = toUser?.uid

       // val iduser = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/project-document/${project.uid}")

        recyclerview_list_documets_project.adapter = adapter
        //получаем доступк ссылке из Firebase бд?!
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?){


                val document = p0.getValue(Document::class.java)
                if(document != null)
                {
                    Log.d(TAG, "Get document")
                    adapter.add(DocumentItem(document))


                    //    adapter.add(ProjectItem(project))
                  /*  val uIdProject = project.uid

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
                                intent.putExtra(USER_KEY, projectItem.project)
                                startActivity(intent)

                                finish()
                            }


                        }
                        override fun onCancelled(p0: DatabaseError) {}
                    })*/

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

class DocumentItem(val document: Document): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.project_textView_edit.text = document.name
    }
    override fun getLayout(): Int {
        return R.layout.project_row_edit_project
    }
}
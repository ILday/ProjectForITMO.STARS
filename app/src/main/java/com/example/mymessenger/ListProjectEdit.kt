package com.example.mymessenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import com.example.mymessenger.models.Department
import com.example.mymessenger.models.Project
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_list_project_edit.*
import kotlinx.android.synthetic.main.project_row_edit_project.view.*

class ListProjectEdit: AppCompatActivity(){

    private val TAG = "ListProjectEdit"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_project_edit)

        recyclerview_list_project_edit.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


        fetchDepartments()
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchDepartments(){
        val ref = FirebaseDatabase.getInstance().getReference("/projects")
        //получаем доступк ссылке из Firebase бд?!
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d(TAG, it.toString())
                    val project = it.getValue(Project::class.java)
                    if(project != null)
                    {
                        adapter.add(ProjectItem(project))
                    }
                }

                adapter.setOnItemClickListener{item, view ->

                    val projectItem = item as ProjectItem

                    val intent = Intent(view.context, EditProjectAdmin::class.java)  //view.context взять контекст
                    intent.putExtra(USER_KEY, projectItem.project)
                    startActivity(intent)

                    finish()
                }

                recyclerview_list_project_edit.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}

class ProjectItem(val project: Project): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.project_textView_edit.text = project.name
    }
    override fun getLayout(): Int {
        return R.layout.project_row_edit_project
    }
}
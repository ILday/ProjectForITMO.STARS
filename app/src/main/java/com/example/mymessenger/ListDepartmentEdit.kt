package com.example.mymessenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import com.example.mymessenger.models.Department
import com.example.mymessenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_list_department_edit.*
import kotlinx.android.synthetic.main.department_row_edit_department.view.*

class ListDepartmentEdit: AppCompatActivity(){

    private val TAG = "ListDepartmentEdit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_department_edit)

        recyclerview_list_department_edit.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


        fetchDepartments()
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchDepartments(){
        val ref = FirebaseDatabase.getInstance().getReference("/departments")
        //получаем доступк ссылке из Firebase бд?!
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d(TAG, it.toString())
                    val department = it.getValue(Department::class.java)
                    if(department != null)
                    {
                        adapter.add(DepartmentItem(department))
                    }
                }

                adapter.setOnItemClickListener{item, view ->

                    val departmentItem = item as DepartmentItem

                    val intent = Intent(view.context, EditDepartmentAdmin::class.java)  //view.context взять контекст
                    intent.putExtra(USER_KEY, departmentItem.department)
                    startActivity(intent)

                       finish()
                }

                recyclerview_list_department_edit.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}

class DepartmentItem(val department: Department): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.department_textView_edit.text = department.name
    }
    override fun getLayout(): Int {
        return R.layout.department_row_edit_department
    }
}


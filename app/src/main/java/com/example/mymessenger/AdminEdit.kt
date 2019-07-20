package com.example.mymessenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.mymessenger.models.Department
import com.example.mymessenger.models.Project
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_admin_edit.*

class AdminEdit : AppCompatActivity(){

    private val TAG = "AdminEdit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit)

        create_department_button.setOnClickListener {
            val reference = FirebaseDatabase.getInstance().getReference("/departments").push()
            val department = Department(reference.key!!, name_department_edittext_edit.text.toString())

            reference.setValue(department)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved new department: ${reference.key}")
                    Toast.makeText(this, "Department saved", Toast.LENGTH_SHORT).show()
                    name_department_edittext_edit.text?.clear()
                }


        }

        create_project_button.setOnClickListener {
            val reference = FirebaseDatabase.getInstance().getReference("/projects").push()
            val project = Project(reference.key!!, name_project_edittext_edit.text.toString())

            reference.setValue(project)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved new project: ${reference.key}")
                    Toast.makeText(this, "Project saved", Toast.LENGTH_SHORT).show()
                    name_project_edittext_edit.text?.clear()
                }
        }

        list_users_button.setOnClickListener {
            val intent = Intent(this, ListUserEdit::class.java)
            startActivity(intent)
        }
        list_departments_button.setOnClickListener {
            val intent = Intent(this, ListDepartmentEdit::class.java)
            startActivity(intent)
        }
        list_projects_button.setOnClickListener {
            val intent = Intent(this, ListProjectEdit::class.java)
            startActivity(intent)
        }
    }
}
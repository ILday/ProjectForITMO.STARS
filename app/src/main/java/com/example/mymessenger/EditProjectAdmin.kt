package com.example.mymessenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.example.mymessenger.Interface.IFirebaseLoadDone
import com.example.mymessenger.models.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_edit_project.*

class EditProjectAdmin: AppCompatActivity(){

    private val TAG = "EditProjectAdmin"
    private lateinit var mProject: Project //из бд текущий
    private  lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_project)


        mProject = intent.getParcelableExtra<Project>(ListProjectEdit.USER_KEY)
        name_project_edittext_edit.setText(mProject!!.name, TextView.BufferType.EDITABLE)

        edit_list_users_button.setOnClickListener {
            val intent = Intent(this, ListUsersProject::class.java)
            intent.putExtra(USER_KEY, mProject)
        Log.d("test21","Запуск")
            startActivity(intent)
        }

    }

    companion object {
        val USER_KEY = "USER_KEY"
    }
}
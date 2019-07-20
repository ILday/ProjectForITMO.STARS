package com.example.mymessenger

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.mymessenger.models.Project
import kotlinx.android.synthetic.main.activity_watch_project.*

class WatchProject: AppCompatActivity(){

    private lateinit var project: Project


    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_project)

        project = intent.getParcelableExtra<Project>(ListProjects.USER_KEY)


        watch_chat_button.setOnClickListener {
            val intent = Intent(this, ChatProject::class.java)
            intent.putExtra(USER_KEY, project)
            startActivity(intent)
        }

        add_document_button.setOnClickListener{
            val intent = Intent(this, ProjectAddDocument::class.java)
            intent.putExtra(USER_KEY, project)
            startActivity(intent)
        }

        list_documets_button.setOnClickListener{
            val intent = Intent(this, DocumentsProject::class.java)
            intent.putExtra(USER_KEY, project)
            startActivity(intent)
        }

    }

}
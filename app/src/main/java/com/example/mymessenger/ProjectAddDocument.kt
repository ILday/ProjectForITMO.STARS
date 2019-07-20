package com.example.mymessenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.mymessenger.models.Document
import com.example.mymessenger.models.Project
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_project_add_documet.*
import java.util.*

class ProjectAddDocument: AppCompatActivity() {

    val DOC : Int = 0
    val XLS : Int = 1
    val PDF : Int = 2

    var TAG = "ProjectAddDocument"

    lateinit var uri : Uri
    private lateinit var project: Project
    lateinit var mStorage : StorageReference
    var urlDocument = ""

    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_add_documet)
        add_document.setEnabled(false)
        val doc_add = findViewById<View>(R.id.add_doc) as Button
        val xls_add = findViewById<View>(R.id.add_xls) as Button
        val pdf_add = findViewById<View>(R.id.add_pdf) as Button

        project = intent.getParcelableExtra<Project>(ListProjects.USER_KEY)


        mStorage = FirebaseStorage.getInstance().getReference("Uploads")

        add_doc.setOnClickListener (View.OnClickListener {
            view: View? -> val intent = Intent()
            intent.setType("doc/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select DOC"), DOC)
        })

        add_xls.setOnClickListener (View.OnClickListener {
                view: View? -> val intent = Intent()
            intent.setType("xls/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select XLS"), XLS)
        })

        add_pdf.setOnClickListener (View.OnClickListener {
                view: View? -> val intent = Intent()
            intent.setType("pdf/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF)
        })

        add_document.setOnClickListener (View.OnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("/project-document/${project.uid}").push()

                val document = Document(ref.key!!, name_document_edittext_project_add_document.text.toString(), urlDocument.toString())
                ref.setValue(document)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved document: ${ref.key}")
                    name_document_edittext_project_add_document.text?.clear()
                }
        })



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val uriTxt = findViewById<View>(R.id.uriTxt) as TextView
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == DOC){
                uri = data!!.data
                uriTxt.text = uri.toString()
                upload()
            }else if(requestCode == XLS){
                uri = data!!.data
                uriTxt.text = uri.toString()
                upload()
            }else if(requestCode == PDF){
                uri = data!!.data
                uriTxt.text = uri.toString()
                upload()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun upload() {
        var mReference = mStorage.child(uri.lastPathSegment)
        try {
          /*  val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/Uploads/$filename") //ссылка на хранилище бд

            ref.putFile(uri!!).addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener{
                    Log.d(TAG, "File location: $it")

                   // saveUserToFirebaseDatabase(it.toString())
                }
            }*/

             mReference.putFile(uri).addOnSuccessListener {
                    taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.uploadSessionUri
                val dwnTxt = findViewById<View>(R.id.dwnTxt) as TextView
                dwnTxt.text = url.toString()
                Toast.makeText(this, "Now you can add this file to the project!", Toast.LENGTH_LONG).show()
                add_document.setEnabled(true)
                mReference.downloadUrl.addOnSuccessListener{
                     Log.d(TAG, "File location: $it")
                     urlDocument = it.toString()
                 }
            }
        }catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }

    }

}
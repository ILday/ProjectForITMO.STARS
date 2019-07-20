package com.example.mymessenger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.mymessenger.models.Department
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_edit_department.*

class EditDepartmentAdmin: AppCompatActivity(){

    private val TAG = "EditUserAdmin"
    private lateinit var mDepartment: Department //из бд текущий
    private  lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_department)


        mDepartment = intent.getParcelableExtra<Department>(ListDepartmentEdit.USER_KEY)
        name_department_edittext_edit.setText(mDepartment!!.name, TextView.BufferType.EDITABLE)
    }
}
package com.example.mymessenger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.mymessenger.Interface.IFirebaseLoadDone
import com.example.mymessenger.models.Department
import com.example.mymessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_user_admin.*

class EditUserAdmin: AppCompatActivity(), IFirebaseLoadDone {
    override fun onFirebaseLoadSuccess(departmentList: List<Department>) {
        val department_name_titles = getDepartmentNameList(departmentList)

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, department_name_titles)

        department_user_spinner_edit.adapter = adapter
    }

    private fun getDepartmentNameList(departmentList: List<Department>): List<String> {
        val result = ArrayList<String>()
        for(department in departmentList)
            result.add(department.name!!)
        return result
    }

    override fun onFirebaseLoadFailed(message: String) {
    }

    private val TAG = "EditUserAdmin"

    internal lateinit var spinnerDepartments : Spinner

    lateinit var departmentRef: DatabaseReference
    lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    private lateinit var mUser: User //из бд текущий
    private  lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_admin)


        mUser = intent.getParcelableExtra<User>(ListUserEdit.USER_KEY)
        name_department_edittext_edit.setText(mUser!!.position, TextView.BufferType.EDITABLE)
      //  spinnerDepartments.setSelection(mUser!!.department)
        /*   spinnerDepartments - findViewById(R.id.department_user_spinner_edit) as Spinner
           val count = arrayOf("№1","№2","№3","№4","№5")
           val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, count)
           spinnerDepartments.adapter = adapter*/

        iFirebaseLoadDone = this

        departmentRef = FirebaseDatabase.getInstance().getReference("departments")

        departmentRef.addValueEventListener(object: ValueEventListener{
            var departmentList:MutableList<Department> = ArrayList<Department>()
            override fun onCancelled(p0: DatabaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(departmentSnapShot in p0.children)
                    departmentList.add(departmentSnapShot.getValue<Department>(Department::class.java!!)!!)
                iFirebaseLoadDone.onFirebaseLoadSuccess(departmentList)
            }

        })

        edit_user_button.setOnClickListener {
            Log.d("EditUserAdmin", "Edit user")
            updateUser()
        }

    }

    private fun updateUser() {
        Log.d("EditUserAdmin", "2")


        if(name_department_edittext_edit.text!=null){
            Log.d("EditUserAdmin", "3")
            val updatesMap = mutableMapOf<String, Any>()
                if(name_department_edittext_edit.text.toString() != mUser.position) updatesMap["position"] = name_department_edittext_edit.toString()
                if(spinnerDepartments.getSelectedItem().toString() != mUser.department) updatesMap["department"] = spinnerDepartments.getSelectedItem().toString()

                mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap)
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(this, "Profile update", Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
        }else{
            Log.d("EditUserAdmin", "3 /2")
            Toast.makeText(this, "Enter position", Toast.LENGTH_SHORT).show()
        }


    }


}
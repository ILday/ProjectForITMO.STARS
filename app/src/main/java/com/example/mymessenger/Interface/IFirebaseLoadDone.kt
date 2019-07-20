package com.example.mymessenger.Interface

import com.example.mymessenger.models.Department

interface IFirebaseLoadDone {
    fun onFirebaseLoadSuccess(departmentList:List<Department>)
    fun onFirebaseLoadFailed(message:String)
}
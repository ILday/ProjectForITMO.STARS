package com.example.mymessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.FieldPosition

@Parcelize
class User(val uid: String, val username: String, val email: String, val phoneNumber: String, val power: Int?, val profileImageUrl: String, val position: String, val department: String): Parcelable {
    constructor() : this("","","", "", 0, "", "", "")
}
//phoneNumber: Long =0L
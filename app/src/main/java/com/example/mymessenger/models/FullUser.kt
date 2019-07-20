package com.example.mymessenger.models


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class FullUser(val uid: String, val username: String, val email: String, val profileImageUrl: String): Parcelable {
    constructor() : this("","","","")
}
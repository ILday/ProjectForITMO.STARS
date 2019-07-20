package com.example.mymessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserProject(val uid: String, val idproject: String): Parcelable {
    constructor() : this("","")
}
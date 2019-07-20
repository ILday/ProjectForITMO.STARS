package com.example.mymessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Department(val uid: String, val name: String): Parcelable {
    constructor() : this("","")
}
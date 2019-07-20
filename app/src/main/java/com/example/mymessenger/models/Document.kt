package com.example.mymessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Document(val uid: String, val name: String, val fileUrl: String): Parcelable {
    constructor() : this("","", "")
}
package com.example.mymessenger.models

class ImageMessage(val id: String, val imageUrl: String, val fromId: String, val toId: String, val timestamp: Long, val time: String){
    constructor(): this("","","","", -1, "")
}
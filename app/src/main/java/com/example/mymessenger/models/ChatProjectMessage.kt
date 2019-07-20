package com.example.mymessenger.models

class ChatProjectMessage (val id: String, val text: String, val fromId: String, val timestamp: Long, val time: String){
    constructor(): this("","","", -1, "")
}
package com.example.mymessenger.models

import java.util.*

class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long, val time: String, val type: String){
    constructor(): this("","","","", -1, "", "")
}
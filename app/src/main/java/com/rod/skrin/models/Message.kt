package com.rod.skrin.models

import java.util.*

data class Message(
    val authorId:String = "",
    val message:String = "",
    val profileImageURL:String = "",
    val sentAt:Date = Date())
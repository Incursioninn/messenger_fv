package com.example.massmess.allPackage.models

data class DefaultModel(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "null",

    var messageText : String = "",
    var messageType : String = "",
    var from : String = "",
    var timeStamp : Any = "",
    var fileUrl : String = "empty",

    var lastMessage : String = "",
    var choiced : Boolean = false


) {
    override fun equals(other: Any?): Boolean {
        return (other as DefaultModel).id == id
    }
}


package com.example.massmess.allPackage.ui.fragments.message_recycler_view.views

import com.example.massmess.allPackage.models.DefaultModel
import com.example.massmess.allPackage.utilits.TYPE_MESSAGE_FILE
import com.example.massmess.allPackage.utilits.TYPE_MESSAGE_IMAGE
import com.example.massmess.allPackage.utilits.TYPE_MESSAGE_VOICE

class ViewFactory {
    companion object {
        fun getView(message : DefaultModel) : MessageView {
            return when(message.messageType) {
                TYPE_MESSAGE_IMAGE -> ViewImageMessage(message.id , message.from , message.timeStamp.toString() , message.fileUrl , message.username)
                TYPE_MESSAGE_VOICE -> ViewVoiceMessage(message.id , message.from , message.timeStamp.toString() , message.fileUrl , message.username)
                TYPE_MESSAGE_FILE -> ViewFileMessage(message.id , message.from , message.timeStamp.toString() , message.fileUrl , message.messageText , message.username)
                else -> {
                    ViewTextMessage(message.id , message.from , message.timeStamp.toString() , message.fileUrl , message.messageText , message.username)
                }

            }

        }

    }
}
package com.example.massmess.allPackage.ui.fragments.message_recycler_view.viewholders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.allPackage.database.CHILD_FULLNAME
import com.example.massmess.allPackage.database.NODE_USERS
import com.example.massmess.allPackage.database.REF_DATABASE_ROOT
import com.example.massmess.allPackage.database.UID
import com.example.massmess.allPackage.ui.fragments.message_recycler_view.views.MessageView
import com.example.massmess.allPackage.utilits.AppValueEventListener
import com.example.massmess.allPackage.utilits.asTime
import kotlinx.android.synthetic.main.message_item_text.view.*

class TextMessageHolder(view : View) : RecyclerView.ViewHolder(view) , MessageHolder {

    private val blockUserMessage: ConstraintLayout = view.block_user_message
    private val chatUserMessage: TextView = view.chats_user_message
    private val chatUserMessageTime: TextView = view.chats_user_message_time

    private val blockReceivedMessage: ConstraintLayout = view.block_received_message
    private val chatReceivedMessage: TextView = view.chats_received_message
    private val chatReceivedMessageTime: TextView = view.chats_received_message_time

    private val chatUserMessageName : TextView = view.chats_user_message_name
    private val chatReceivedMessageName : TextView = view.chats_received_message_name


    override fun showMessage(view: MessageView) {
        if (view.from == UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.messageText
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_FULLNAME).get().addOnSuccessListener {
                chatUserMessageName.text = it.value.toString()
            }
            chatUserMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blockUserMessage.visibility = View.GONE
            blockReceivedMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = view.messageText
            chatReceivedMessageName.text = view.userName
            REF_DATABASE_ROOT.child(NODE_USERS).child(view.from).child(CHILD_FULLNAME).get().addOnSuccessListener {
                chatReceivedMessageName.text = it.value.toString()
            }
            chatReceivedMessageTime.text =
                view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }
}
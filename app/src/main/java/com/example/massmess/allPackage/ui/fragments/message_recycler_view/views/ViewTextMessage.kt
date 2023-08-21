package com.example.massmess.allPackage.ui.fragments.message_recycler_view.views

data class ViewTextMessage(
    override val id: String,
    override val from: String,
    override val timeStamp: String,
    override val fileUrl: String ="",
    override val messageText: String,
    override val userName : String,

) : MessageView {

    override fun getViewType(): Int {
        return MessageView.MESSAGE_TEXT
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }

}
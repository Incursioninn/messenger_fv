package com.example.massmess.allPackage.ui.fragments.message_recycler_view.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.allPackage.database.CHILD_FULLNAME
import com.example.massmess.allPackage.database.NODE_USERS
import com.example.massmess.allPackage.database.REF_DATABASE_ROOT
import com.example.massmess.allPackage.database.UID
import com.example.massmess.allPackage.ui.fragments.message_recycler_view.views.MessageView
import com.example.massmess.allPackage.utilits.asTime
import com.example.massmess.allPackage.utilits.downloadAndSetImage
import kotlinx.android.synthetic.main.message_item_image.view.*

class ImageMessageHolder (view : View) : RecyclerView.ViewHolder(view) , MessageHolder {

    private val blockReceivedImage: ConstraintLayout = view.block_received_image
    private val blockUserImage: ConstraintLayout = view.block_user_image
    private val chatReceivedImage: ImageView = view.chat_received_image
    private val chatUserImage: ImageView = view.chat_user_image
    private val chatReceivedImageTime: TextView = view.chats_received_image_time
    private  val chatUserImageTime: TextView = view.chats_user_image_time
    private val chatUserImageName : TextView = view.chats_user_image_name
    private val chatReceivedImageName : TextView = view.chats_received_image_name



    override fun showMessage(view: MessageView) {
        if (view.from == UID) {


            blockReceivedImage.visibility = View.GONE
            blockUserImage.visibility = View.VISIBLE
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_FULLNAME).get().addOnSuccessListener {
                chatUserImageName.text = it.value.toString()
            }
            chatUserImage.downloadAndSetImage(view.fileUrl)
            chatUserImageTime.text =
                view.timeStamp.asTime()


        } else {

            blockReceivedImage.visibility = View.VISIBLE
            blockUserImage.visibility = View.GONE
            REF_DATABASE_ROOT.child(NODE_USERS).child(view.from).child(CHILD_FULLNAME).get().addOnSuccessListener {
                chatReceivedImageName.text = it.value.toString()
            }
            chatReceivedImage.downloadAndSetImage(view.fileUrl)
            chatReceivedImageTime.text =
                view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }

}
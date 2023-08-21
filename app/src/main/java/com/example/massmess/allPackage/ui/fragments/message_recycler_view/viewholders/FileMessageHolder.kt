package com.example.massmess.allPackage.ui.fragments.message_recycler_view.viewholders

import android.os.Environment
import android.sax.EndElementListener
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.allPackage.database.*
import com.example.massmess.allPackage.ui.fragments.message_recycler_view.views.MessageView
import com.example.massmess.allPackage.utilits.WRITE_FILES
import com.example.massmess.allPackage.utilits.asTime
import com.example.massmess.allPackage.utilits.checkPermission
import com.example.massmess.allPackage.utilits.showToast
import kotlinx.android.synthetic.main.message_item_file.view.*
import java.io.File

class FileMessageHolder (view : View) : RecyclerView.ViewHolder(view) , MessageHolder {

    private lateinit var mFile : File


    private val blockReceivedFile: ConstraintLayout = view.block_received_file
    private val blockUserFile: ConstraintLayout = view.block_user_file
    private val chatReceivedFileTime: TextView = view.chats_received_file_time
    private val chatUserFileTime: TextView = view.chats_user_file_time

    private val chatUserFileName : TextView = view.chat_user_filename
    private val chatUserBtnDownload : ImageView = view.chat_user_btn_download
    private val chatUserProgressBar : ProgressBar = view.chat_user_progress_bar

    private val chatReceivedFileName : TextView = view.chat_received_filename
    private val chatReceivedBtnDownload : ImageView = view.chat_received_btn_download
    private val chatReceivedProgressBar : ProgressBar = view.chat_received_progress_bar

    private val chatReceivedFileUsername : TextView = view.chats_received_file_username
    private val chatUserFileUsername : TextView = view.chats_user_file_username




    override fun showMessage(view: MessageView) {
        if (view.from == UID) {


            blockReceivedFile.visibility = View.GONE
            blockUserFile.visibility = View.VISIBLE
            chatUserFileTime.text = view.timeStamp.asTime()
            chatUserFileName.text = view.messageText
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_FULLNAME).get().addOnSuccessListener {
                chatUserFileUsername.text = it.value.toString()
            }


        } else {

            blockReceivedFile.visibility = View.VISIBLE
            blockUserFile.visibility = View.GONE
            chatReceivedFileTime.text = view.timeStamp.asTime()
            chatReceivedFileName.text = view.messageText
            REF_DATABASE_ROOT.child(NODE_USERS).child(view.from).child(CHILD_FULLNAME).get().addOnSuccessListener {
                chatReceivedFileUsername.text = it.value.toString()
            }
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == UID) {
            chatUserBtnDownload.setOnClickListener {
                clickOnFileBtn(view)
            }
        }else {
            chatReceivedBtnDownload.setOnClickListener {
                clickOnFileBtn(view)
            }
        }
    }

    private fun clickOnFileBtn(view: MessageView) {
        if (view.from == UID) {
            chatUserBtnDownload.visibility = View.INVISIBLE
            chatUserProgressBar.visibility = View.VISIBLE
        } else {
            chatReceivedBtnDownload.visibility = View.INVISIBLE
            chatReceivedProgressBar.visibility = View.VISIBLE
        }
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        mFile = File(path  , view.messageText)

        try {
            if(checkPermission(WRITE_FILES)){
                mFile.createNewFile()
                getFileFromStorage(mFile , view.fileUrl) {
                    if (view.from == UID) {
                        chatUserBtnDownload.visibility = View.VISIBLE
                        chatUserProgressBar.visibility = View.INVISIBLE
                    } else {
                        chatReceivedBtnDownload.visibility = View.VISIBLE
                        chatReceivedProgressBar.visibility = View.INVISIBLE
                    }
                }

            }
        }catch (e:Exception) {
            showToast(e.message.toString())
        }

    }


    override fun onDetach() {

        chatUserBtnDownload.setOnClickListener(null)
        chatReceivedBtnDownload.setOnClickListener(null)


    }

}
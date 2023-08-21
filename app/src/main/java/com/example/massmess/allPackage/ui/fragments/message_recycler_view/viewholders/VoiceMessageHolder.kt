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
import com.example.massmess.allPackage.utilits.VoicePlayer
import com.example.massmess.allPackage.utilits.asTime
import kotlinx.android.synthetic.main.message_item_voice.view.*

class VoiceMessageHolder (view : View) : RecyclerView.ViewHolder(view) , MessageHolder {

    private val mVoicePlayer : VoicePlayer = VoicePlayer()

    private val blockReceivedVoice: ConstraintLayout = view.block_received_voice
    private val blockUserVoice: ConstraintLayout = view.block_user_voice
    private val chatReceivedVoiceTime: TextView = view.chats_received_voice_time
    private val chatUserVoiceTime: TextView = view.chats_user_voice_time

    private val chatReceivedBtnPlay : ImageView = view.chat_received_btn_play
    private val chatReceivedBtnStop : ImageView = view.chat_received_btn_stop

    private val chatUserBtnPlay : ImageView = view.chat_user_btn_play
    private val chatUserBtnStop : ImageView = view.chat_user_btn_stop

    private val chatUserVoiceName : TextView = view.chats_user_voice_name
    private val chatReceivedVoiceName : TextView = view.chats_received_voice_name

    override fun showMessage(view: MessageView) {
        mVoicePlayer.init()
        if (view.from == UID) {


            blockReceivedVoice.visibility = View.GONE
            blockUserVoice.visibility = View.VISIBLE
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_FULLNAME).get().addOnSuccessListener {
                chatUserVoiceName.text = it.value.toString()
            }
            chatUserVoiceTime.text =
                view.timeStamp.asTime()


        } else {

            blockReceivedVoice.visibility = View.VISIBLE
            blockUserVoice.visibility = View.GONE
            REF_DATABASE_ROOT.child(NODE_USERS).child(view.from).child(CHILD_FULLNAME).get().addOnSuccessListener {
                chatReceivedVoiceName.text = it.value.toString()
            }
            chatReceivedVoiceTime.text =
                view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

        if(view.from == UID) {
            chatUserBtnPlay.setOnClickListener {

                chatUserBtnPlay.visibility = View.GONE
                chatUserBtnStop.visibility = View.VISIBLE
                chatUserBtnStop.setOnClickListener{
                    stop {
                        chatUserBtnStop.setOnClickListener(null)
                        chatUserBtnPlay.visibility = View.VISIBLE
                        chatUserBtnStop.visibility = View.GONE
                    }
                }

                play(view){
                    chatUserBtnPlay.visibility = View.VISIBLE
                    chatUserBtnStop.visibility = View.GONE

                }

            }
        }
        else {
            chatReceivedBtnPlay.setOnClickListener {
                chatReceivedBtnPlay.visibility = View.GONE
                chatReceivedBtnStop.visibility = View.VISIBLE
                chatReceivedBtnStop.setOnClickListener{
                    stop {
                        chatReceivedBtnStop.setOnClickListener(null)
                        chatReceivedBtnPlay.visibility = View.VISIBLE
                        chatReceivedBtnStop.visibility = View.GONE
                    }
                }

                play(view){
                    chatReceivedBtnPlay.visibility = View.VISIBLE
                    chatReceivedBtnStop.visibility = View.GONE
                }
            }
        }
    }

    private fun play(view: MessageView, function: () -> Unit) {
        mVoicePlayer.play(view.id , view.fileUrl) {
            function()
        }
    }

    private fun stop(function: () -> Unit) {
        mVoicePlayer.stop {
            function()
        }
    }


    override fun onDetach() {
        chatReceivedBtnPlay.setOnClickListener(null)
        chatUserBtnPlay.setOnClickListener(null)
        mVoicePlayer.release()

    }

}
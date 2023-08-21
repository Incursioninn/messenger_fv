package com.example.massmess.allPackage.ui.fragments.message_recycler_view.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.R
import com.example.massmess.allPackage.ui.fragments.message_recycler_view.views.MessageView

class HolderFactory {
    companion object {

        fun getHolder(parent : ViewGroup , viewType : Int) : RecyclerView.ViewHolder {
            return when (viewType) {
                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_image, parent, false)
                    ImageMessageHolder(view)
                }
                MessageView.MESSAGE_VOICE -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_voice, parent, false)
                    VoiceMessageHolder(view)
                }
                MessageView.MESSAGE_FILE -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_file, parent, false)
                    FileMessageHolder(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_text, parent, false)
                    TextMessageHolder(view)
                }
            }
        }
    }

}
package com.example.massmess.allPackage.ui.fragments.main_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.R
import com.example.massmess.allPackage.models.DefaultModel
import com.example.massmess.allPackage.ui.fragments.groups.GroupChatFragment
import com.example.massmess.allPackage.ui.fragments.individual_chat.IndividualChatFragment
import com.example.massmess.allPackage.utilits.TYPE_CHAT
import com.example.massmess.allPackage.utilits.TYPE_GROUP
import com.example.massmess.allPackage.utilits.downloadAndSetImage
import com.example.massmess.allPackage.utilits.switchFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.main_list_item.view.*

class ChatsAdapter : RecyclerView.Adapter<ChatsAdapter.ChatsHolder> () {

    private var itemsList = mutableListOf<DefaultModel>()

    class ChatsHolder(view:View):RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.main_list_item_name
        val itemLastMessage : TextView = view.main_list_last_message
        val itemPhoto: CircleImageView = view.main_list_contact_photo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_list_item , parent , false)
        val holder = ChatsHolder(view)
        holder.itemView.setOnClickListener {

            when(itemsList[holder.absoluteAdapterPosition].messageType) {
                TYPE_CHAT -> {
                    switchFragment(IndividualChatFragment(itemsList[holder.absoluteAdapterPosition]))
                }
                TYPE_GROUP -> {
                    switchFragment(GroupChatFragment(itemsList[holder.absoluteAdapterPosition]))
                }
            }



        }
        return holder
    }

    override fun onBindViewHolder(holder: ChatsHolder, position: Int) {
        holder.itemName.text = itemsList[position].fullname
        holder.itemLastMessage.text = itemsList[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(itemsList[position].photoUrl)
    }

    override fun getItemCount(): Int = itemsList.size

    fun updateItemsList (item : DefaultModel) {
        itemsList.add(item)
        notifyItemInserted(itemsList.size)
    }

}
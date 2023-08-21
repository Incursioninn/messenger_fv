package com.example.massmess.allPackage.ui.fragments.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.R
import com.example.massmess.allPackage.models.DefaultModel
import com.example.massmess.allPackage.utilits.downloadAndSetImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_groups_item.view.*

class AddContactsAdapter : RecyclerView.Adapter<AddContactsAdapter.AddContactsHolder>() {

    private var itemsList = mutableListOf<DefaultModel>()

    class AddContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.add_groups_item_name
        val itemLastMessage: TextView = view.add_groups_last_message
        val itemPhoto: CircleImageView = view.add_groups_contact_photo
        val itemChoice: CircleImageView = view.add_groups_item_choice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.add_groups_item, parent, false)
        val holder = AddContactsHolder(view)
        holder.itemView.setOnClickListener {
            if (itemsList[holder.absoluteAdapterPosition].choiced) {
                holder.itemChoice.visibility = View.INVISIBLE
                itemsList[holder.absoluteAdapterPosition].choiced = false
                AddContactsFragment.contactsList.remove(itemsList[holder.absoluteAdapterPosition])
            } else {
                holder.itemChoice.visibility = View.VISIBLE
                itemsList[holder.absoluteAdapterPosition].choiced = true
                AddContactsFragment.contactsList.add(itemsList[holder.absoluteAdapterPosition])
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
        holder.itemName.text = itemsList[position].fullname
        holder.itemLastMessage.text = itemsList[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(itemsList[position].photoUrl)
    }

    override fun getItemCount(): Int = itemsList.size

    fun updateItemsList(item: DefaultModel) {
        itemsList.add(item)
        notifyItemInserted(itemsList.size)
    }

}
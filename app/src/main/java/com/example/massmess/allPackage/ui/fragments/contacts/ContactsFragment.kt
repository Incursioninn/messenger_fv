package com.example.massmess.allPackage.ui.fragments.contacts


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.R
import com.example.massmess.allPackage.database.*
import com.example.massmess.allPackage.utilits.downloadAndSetImage
import com.example.massmess.allPackage.utilits.switchFragment
import com.example.massmess.allPackage.models.DefaultModel
import com.example.massmess.allPackage.ui.fragments.base.BaseFragment
import com.example.massmess.allPackage.ui.fragments.individual_chat.IndividualChatFragment
import com.example.massmess.allPackage.utilits.ACTIVITY
import com.example.massmess.allPackage.utilits.AppValueEventListener

import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.contact_item.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*


class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mAdapter : FirebaseRecyclerAdapter<DefaultModel, ContactsHolder>
    private lateinit var mRefToContacts : DatabaseReference
    private lateinit var mRefToUsers : DatabaseReference
    private lateinit var mRefToUserListener : AppValueEventListener
    private var mapOfListeners = HashMap<DatabaseReference , AppValueEventListener>()

    override fun onResume() {
        super.onResume()
        ACTIVITY.title = "Контакты"
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = contacts_recycler_view
        mRefToContacts = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)

        val options = FirebaseRecyclerOptions.Builder<DefaultModel>()
            .setQuery(mRefToContacts, DefaultModel::class.java)
            .build()
        mAdapter = object : FirebaseRecyclerAdapter<DefaultModel, ContactsHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item , parent,false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: DefaultModel
            ) {
                mRefToUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)
                mRefToUserListener = AppValueEventListener {
                    val contact = it.getDefaultModel()

                    if (contact.fullname.isEmpty()){
                        holder.name.text = model.fullname
                    }
                    else holder.name.text = contact.fullname

                    holder.name.text = contact.fullname
                    holder.status.text = contact.state
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener {
                        switchFragment(IndividualChatFragment(model))
                    }
                }
                mRefToUsers.addValueEventListener(mRefToUserListener)
                mapOfListeners[mRefToUsers] = mRefToUserListener


            }

        }

        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()
    }

    class ContactsHolder(view : View):RecyclerView.ViewHolder(view){
        val name : TextView = view.contact_fullname
        val status : TextView = view.contact_status
        val photo : CircleImageView = view.contact_photo
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        mapOfListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }
}



package com.example.massmess.allPackage.ui.fragments.groups

import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.R
import com.example.massmess.allPackage.database.*
import com.example.massmess.allPackage.models.DefaultModel
import com.example.massmess.allPackage.ui.fragments.base.BaseFragment
import com.example.massmess.allPackage.utilits.*
import kotlinx.android.synthetic.main.fragment_add_contacts.*


class AddContactsFragment : BaseFragment(R.layout.fragment_add_contacts) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private val mRefContactsList = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID)
    private var mListItems = listOf<DefaultModel>()


    override fun onResume() {
        contactsList.clear()
        super.onResume()
        ACTIVITY.title = "Добавить пользователя"
        closeKeyboard()
        initRecyclerView()
        add_contacts_btn_next.setOnClickListener {
            if (contactsList.isEmpty()) {
                showToast("Добавьте участников")
            } else {
                switchFragment(CreateGroupFragment(contactsList))
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = add_contacts_recycler_view
        mAdapter = AddContactsAdapter()
        mRefContactsList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getDefaultModel() }
            mListItems.forEach { model ->

                
                mRefUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                        val newModel = dataSnapshot1.getDefaultModel()
                        mRefMessages.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                                val tempList = dataSnapshot2.children.map { it.getDefaultModel() }
                                if (tempList.isEmpty()) {
                                    newModel.lastMessage = "Чат очищен"
                                } else {
                                    newModel.lastMessage = tempList[0].messageText
                                }

                                if (newModel.fullname.isEmpty()) {
                                    newModel.fullname = newModel.phone
                                }
                                mAdapter.updateItemsList(newModel)
                            })
                    })
            }
        })

        mRecyclerView.adapter = mAdapter

    }

    companion object {
        val contactsList = mutableListOf<DefaultModel>()
    }


}


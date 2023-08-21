package com.example.massmess.allPackage.ui.fragments.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.R
import com.example.massmess.allPackage.database.*
import com.example.massmess.allPackage.models.DefaultModel
import com.example.massmess.allPackage.utilits.*
import kotlinx.android.synthetic.main.fragment_chats.*


class ChatsFragment : Fragment(R.layout.fragment_chats) {

    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mAdapter : ChatsAdapter
    private val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID)
    private var mListItems = listOf<DefaultModel>()



    override fun onResume() {
        super.onResume()
        ACTIVITY.title = "Чаты"
        ACTIVITY.mAppDrawer.enableDrawer()
        closeKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = main_list_recycler_view
        mAdapter = ChatsAdapter()
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
                mListItems = dataSnapshot.children.map { it.getDefaultModel() }
                mListItems.forEach { model->

                    when(model.messageType){
                        TYPE_CHAT -> showChat(model)
                        TYPE_GROUP -> showGroup(model)
                    }


                }
        })

        mRecyclerView.adapter = mAdapter

    }

    private fun showGroup(model: DefaultModel) {
        REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id).addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1->
            val newModel = dataSnapshot1.getDefaultModel()
            REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id).child(NODE_MESSAGES)
                .limitToLast(1).addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2->
                val tempList = dataSnapshot2.children.map { it.getDefaultModel() }
                if(tempList.isEmpty()) {
                    newModel.lastMessage = "Чат очищен"
                } else {
                    newModel.lastMessage = tempList[0].messageText
                }
                newModel.messageType = TYPE_GROUP
                mAdapter.updateItemsList(newModel)
            })
        })
    }

    private fun showChat(model: DefaultModel) {
        mRefUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1->
            val newModel = dataSnapshot1.getDefaultModel()
            mRefMessages.child(model.id).limitToLast(1).addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2->
                val tempList = dataSnapshot2.children.map { it.getDefaultModel() }
                if(tempList.isEmpty()) {
                    newModel.lastMessage = "Чат очищен"
                } else {
                    newModel.lastMessage = tempList[0].messageText
                }

                if(newModel.fullname.isEmpty()) {
                    newModel.fullname = newModel.phone
                }
                newModel.messageType = TYPE_CHAT
                mAdapter.updateItemsList(newModel)
            })
        })
    }


}
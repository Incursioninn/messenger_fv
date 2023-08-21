package com.example.massmess.allPackage.ui.fragments.individual_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.allPackage.ui.fragments.message_recycler_view.viewholders.HolderFactory
import com.example.massmess.allPackage.ui.fragments.message_recycler_view.viewholders.MessageHolder
import com.example.massmess.allPackage.ui.fragments.message_recycler_view.views.MessageView

class IndividualChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListMessagesCache = mutableListOf<MessageView>()
    private var mListHolders = mutableListOf<MessageHolder>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return HolderFactory.getHolder(parent , viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessagesCache[position].getViewType()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as MessageHolder).showMessage(mListMessagesCache[position])


    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(mListMessagesCache[holder.absoluteAdapterPosition])
        mListHolders.add((holder as MessageHolder))
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDetach()
        mListHolders.remove((holder as MessageHolder))
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!mListMessagesCache.contains(item)) {
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun destroy() {
        mListHolders.forEach {
            it.onDetach()
        }
    }


}



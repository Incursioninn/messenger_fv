package com.example.massmess.allPackage.ui.fragments.message_recycler_view.viewholders

import com.example.massmess.allPackage.ui.fragments.message_recycler_view.views.MessageView

interface MessageHolder {
    fun showMessage(view : MessageView)
    fun onAttach(view : MessageView)
    fun onDetach()
}
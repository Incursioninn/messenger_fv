package com.example.massmess.allPackage.ui.fragments.settings

import com.example.massmess.R
import com.example.massmess.allPackage.database.*
import com.example.massmess.allPackage.ui.fragments.base.BaseChangeFragment
import com.example.massmess.allPackage.utilits.AppValueEventListener
import com.example.massmess.allPackage.utilits.showToast

import kotlinx.android.synthetic.main.fragment_change_username.*


class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    lateinit var mNewUsername:String



    override fun onResume() {
        super.onResume()
        settings_input_username.setText(USER.username)
    }


    override fun change() {
        mNewUsername = settings_input_username.text.toString().lowercase()
        if(mNewUsername.isEmpty()){
            showToast("Введите имя пользователя")
        }
        else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener{
                    if (it.hasChild(mNewUsername)){
                        showToast("Имя пользователя уже занято")
                    }
                    else {
                        makeChange()
                    }
                })

        }
    }

    private fun makeChange() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(UID)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    updateCurrentUsername(mNewUsername)
                }
            }
    }





}
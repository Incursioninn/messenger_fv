package com.example.massmess.allPackage.ui.fragments.settings

import com.example.massmess.R
import com.example.massmess.allPackage.database.USER
import com.example.massmess.allPackage.database.pushFullnameToDatabase
import com.example.massmess.allPackage.ui.fragments.base.BaseChangeFragment
import com.example.massmess.allPackage.utilits.showToast

import kotlinx.android.synthetic.main.fragment_change_name.*


class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    override fun onResume() {
        super.onResume()
        val fullnameList = USER.fullname.split(" ")
        if(fullnameList.size <= 1 ){
            settings_input_name.setText(fullnameList[0])
        }
        else {
            settings_input_name.setText(fullnameList[0])
            settings_input_surname.setText(fullnameList[1])
        }


    }



    override fun change() {
        val name = settings_input_name.text.toString()
        val surname = settings_input_surname.text.toString()
        if (name.isEmpty()){
            showToast("Введите имя")
        } else {
            val fullname = "$name $surname"
            pushFullnameToDatabase(fullname)

        }
    }




}
package com.example.massmess.allPackage.ui.fragments.settings

import com.example.massmess.R
import com.example.massmess.allPackage.database.USER
import com.example.massmess.allPackage.database.pushBioToDatabase
import com.example.massmess.allPackage.ui.fragments.base.BaseChangeFragment

import kotlinx.android.synthetic.main.fragment_change_bio.*

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {


    override fun onResume() {
        super.onResume()
        settings_input_bio.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val newBio = settings_input_bio.text.toString()

        pushBioToDatabase(newBio)

    }



}
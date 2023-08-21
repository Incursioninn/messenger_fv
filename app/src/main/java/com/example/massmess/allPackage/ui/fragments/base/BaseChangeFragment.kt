package com.example.massmess.allPackage.ui.fragments.base

import android.view.*
import androidx.fragment.app.Fragment
import com.example.massmess.R
import com.example.massmess.allPackage.utilits.ACTIVITY
import com.example.massmess.allPackage.utilits.closeKeyboard


open class BaseChangeFragment(layout : Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        (ACTIVITY).mAppDrawer.disableDrawer()


    }

    override fun onStop() {
        super.onStop()
        closeKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (ACTIVITY).menuInflater.inflate(R.menu.settings_menu_confirm , menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.settings_confirm_change -> change()
        }

        return true
    }

    open fun change() {

    }

}
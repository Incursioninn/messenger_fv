package com.example.massmess.allPackage.ui.fragments.base

import androidx.fragment.app.Fragment
import com.example.massmess.allPackage.utilits.ACTIVITY


open class BaseFragment(layout : Int) : Fragment(layout) {


    override fun onStart() {
        super.onStart()
        (ACTIVITY).mAppDrawer.disableDrawer()

    }



}
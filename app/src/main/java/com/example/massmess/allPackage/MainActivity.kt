package com.example.massmess.allPackage

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.massmess.allPackage.database.AUTH
import com.example.massmess.allPackage.database.initFirebase
import com.example.massmess.allPackage.database.initUserModel
import com.example.massmess.allPackage.ui.fragments.main_list.ChatsFragment
import com.example.massmess.allPackage.ui.fragments.register.EnterPhoneNumberFragment
import com.example.massmess.allPackage.ui.objects.AppDrawer
import com.example.massmess.allPackage.utilits.*
import com.example.massmess.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityMainBinding
    lateinit var mAppDrawer : AppDrawer
    lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ACTIVITY = this
        initFirebase()
        initUserModel {
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunc()
        }


    }




    private fun initFunc() {
        setSupportActionBar(mToolbar)

        if ( AUTH.currentUser != null ) {
            mAppDrawer.create()
            switchFragment(ChatsFragment() ,false)
        }
        else {
            switchFragment(EnterPhoneNumberFragment() , false)
        }

    }



    private fun initFields() {

        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer()

    }

    override fun onStart() {
        super.onStart()
        States.updateState(States.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        States.updateState(States.OFFLINE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(ACTIVITY , READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            initContacts()
        }
    }



}
package com.example.massmess.allPackage.ui.fragments.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.massmess.R
import com.example.massmess.allPackage.database.*
import com.example.massmess.allPackage.ui.fragments.base.BaseFragment
import com.example.massmess.allPackage.utilits.*

import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : BaseFragment(R.layout.fragment_settings) {


    override fun onResume() {
        super.onResume()
        ACTIVITY.title = "Настройки"
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() {
        settings_bio.text = USER.bio
        settings_full_name.text = USER.fullname
        settings_phone_number.text = USER.phone
        settings_status.text = USER.state
        settings_username.text = USER.username
        settings_btn_change_username.setOnClickListener { switchFragment(ChangeUsernameFragment()) }
        settings_btn_change_bio.setOnClickListener { switchFragment(ChangeBioFragment()) }
        settings_change_photo.setOnClickListener { changeUserPhoto() }
        settings_user_photo.downloadAndSetImage(USER.photoUrl)
    }

    private fun changeUserPhoto() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
            .setMinCropWindowSize(0, 0)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(ACTIVITY, this)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_exit -> {
                States.updateState(States.OFFLINE)
                AUTH.signOut()
                restartActivity()
            }
            R.id.settings_menu_change_name -> {
                switchFragment(ChangeNameFragment())
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(UID)

            putFileToStorage(uri, path) {
                getUrlStorage(path) {
                    putUrlIntoDatabase(it) {
                        settings_user_photo.downloadAndSetImage(it)
                        showToast("Данные обновлены")
                        USER.photoUrl = it
                        ACTIVITY.mAppDrawer.updateHeader()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }


}
package com.example.massmess.allPackage.ui.fragments.groups

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.example.massmess.R
import com.example.massmess.allPackage.database.createGroupInDatabase
import com.example.massmess.allPackage.models.DefaultModel
import com.example.massmess.allPackage.ui.fragments.base.BaseFragment
import com.example.massmess.allPackage.ui.fragments.main_list.ChatsFragment
import com.example.massmess.allPackage.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_group.*

class CreateGroupFragment(var contactsList: List<DefaultModel>) :
    BaseFragment(R.layout.fragment_create_group) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private var mUri = Uri.EMPTY


    override fun onResume() {
        super.onResume()
        ACTIVITY.title = "Создать группу"
        closeKeyboard()
        initRecyclerView()
        create_group_photo.setOnClickListener {
            addPhoto()
        }
        create_group_btn_complete.setOnClickListener {
            val groupName = create_group_input_name.text.toString()
            if(groupName.isEmpty()) {
                showToast("Введите имя группы")
            } else {

                createGroupInDatabase(groupName , mUri , contactsList) {
                    switchFragment(ChatsFragment())
                }
            }
        }
        create_group_input_name.requestFocus()
        create_group_counts.text = getPlurals(contactsList.size)
    }



    private fun addPhoto() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
            .setMinCropWindowSize(0, 0)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(ACTIVITY, this)
    }

    private fun initRecyclerView() {
        mRecyclerView = create_group_recycler_view
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        contactsList.forEach { mAdapter.updateItemsList(it) }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            mUri = CropImage.getActivityResult(data).uri
            create_group_photo.setImageURI(mUri)


        }
    }
}
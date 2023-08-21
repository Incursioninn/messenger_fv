package com.example.massmess.allPackage.ui.fragments.individual_chat

import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.massmess.R
import com.example.massmess.allPackage.database.*
import com.example.massmess.allPackage.models.DefaultModel
import com.example.massmess.allPackage.models.User
import com.example.massmess.allPackage.ui.fragments.base.BaseFragment
import com.example.massmess.allPackage.ui.fragments.main_list.ChatsFragment
import com.example.massmess.allPackage.ui.fragments.message_recycler_view.views.ViewFactory
import com.example.massmess.allPackage.utilits.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_individual_chat.*
import kotlinx.android.synthetic.main.toolbar_chats.view.*
import kotlinx.android.synthetic.main.upload_choice.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class IndividualChatFragment(private val contact: DefaultModel) :
    BaseFragment(R.layout.fragment_individual_chat) {

    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: User
    private lateinit var mToolBarInfo: View
    private lateinit var mRefToUser: DatabaseReference
    private lateinit var mRefToMessages: DatabaseReference
    private lateinit var mAdapter: IndividualChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private var mCountMessagesToLoad = 10
    private var mIsUserScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>


    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()

    }

    private fun initFields() {
        setHasOptionsMenu(true)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_choice)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mAppVoiceRecorder = AppVoiceRecorder()
        mSwipeRefreshLayout = chats_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)
        chats_input_message.addTextChangedListener(AppTextWatcher {
            val input = chats_input_message.text.toString()
            if(input.isEmpty() || input == "Идет запись"){
                chats_btn_send.visibility = View.GONE
                chats_btn_attch.visibility = View.VISIBLE
                chats_btn_voice_message.visibility = View.VISIBLE
            }else {
                chats_btn_send.visibility = View.VISIBLE
                chats_btn_attch.visibility = View.GONE
                chats_btn_voice_message.visibility = View.GONE
            }

        })

        chats_btn_attch.setOnClickListener {attach()}
        CoroutineScope(Dispatchers.IO).launch {
            chats_btn_voice_message.setOnTouchListener { v, event ->

                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        chats_input_message.setText("Идет запись")
                        val messageKey = getMessageKey(contact.id)
                        mAppVoiceRecorder.startRecording(messageKey)

                    } else if (event.action == MotionEvent.ACTION_UP) {
                        chats_input_message.setText("")
                        mAppVoiceRecorder.stopRecording{ file , messageKey ->
                            uploadFileToStorage(Uri.fromFile(file) , messageKey , contact.id , TYPE_MESSAGE_VOICE)
                            mSmoothScrollToPosition = true

                        }

                    }
                }
                true
            }
        }
    }

    private fun attach() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        attach_file_btn.setOnClickListener {
            attachFile()
        }
        attach_image_btn.setOnClickListener {
            attachImage()
        }
    }

    private fun attachFile() {

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent , CHOOSE_FILE_REQUEST_CODE)

    }


    private fun attachImage() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
            .setMinCropWindowSize(0, 0)
            .start(ACTIVITY, this)
    }

    private fun initRecyclerView() {
        mRecyclerView = chats_recycler_view
        mAdapter = IndividualChatAdapter()
        mRefToMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(contact.id)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false

        mRecyclerView.layoutManager = mLayoutManager

        mMessagesListener = AppChildEventListener {

            val message = it.getDefaultModel()

            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(ViewFactory.getView(message)) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(ViewFactory.getView(message)) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }


        }



        mRefToMessages.limitToLast(mCountMessagesToLoad).addChildEventListener(mMessagesListener)


        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsUserScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsUserScrolling = true
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener {
            updateData()
        }

    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsUserScrolling = false
        mCountMessagesToLoad += 10
        mRefToMessages.removeEventListener(mMessagesListener)
        mRefToMessages.limitToLast(mCountMessagesToLoad).addChildEventListener(mMessagesListener)


    }

    private fun initToolbar() {
        mToolBarInfo = ACTIVITY.mToolbar.toolbar_chats
        mToolBarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }

        mRefToUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefToUser.addValueEventListener(mListenerInfoToolbar)
        chats_btn_send.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = chats_input_message.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else {
                sendMessage(message, contact.id, TYPE_TEXT) {
                    saveToMainList(contact.id , TYPE_CHAT)
                    chats_input_message.setText("")
                }
            }
        }
    }




    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            mToolBarInfo.toolbar_chats_fullname.text = contact.fullname
        } else mToolBarInfo.toolbar_chats_fullname.text = mReceivingUser.fullname
        mToolBarInfo.toolbar_chats_image.downloadAndSetImage(mReceivingUser.photoUrl)

        mToolBarInfo.toolbar_chats_status.text = mReceivingUser.state
    }

    override fun onPause() {
        super.onPause()
        mToolBarInfo.visibility = View.GONE
        mRefToUser.removeEventListener(mListenerInfoToolbar)
        mRefToMessages.removeEventListener(mMessagesListener)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessageKey(contact.id)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
                    mSmoothScrollToPosition = true
                }
                CHOOSE_FILE_REQUEST_CODE -> {

                    val uri = data.data
                    val messageKey = getMessageKey(contact.id)
                    val filename = getFileNameFromUri(uri!!)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_FILE , filename)
                    mSmoothScrollToPosition = true
                }
            }
        }

    }




    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
        mAdapter.destroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.individual_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_chat -> clearChat(contact.id){
                showToast("Чат очищен")
                switchFragment(ChatsFragment())
            }
            R.id.menu_delete_chat -> deleteChat(contact.id){
                showToast("Чат удален")
                switchFragment(ChatsFragment())
            }
        }
        return true
    }




}



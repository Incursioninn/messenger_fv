package com.example.massmess.allPackage.database

import android.net.Uri
import com.example.massmess.allPackage.models.DefaultModel
import com.example.massmess.allPackage.models.User
import com.example.massmess.allPackage.utilits.ACTIVITY
import com.example.massmess.allPackage.utilits.AppValueEventListener
import com.example.massmess.allPackage.utilits.TYPE_GROUP
import com.example.massmess.allPackage.utilits.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User()
    UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

inline fun putUrlIntoDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_PHOTO_URL)
        .setValue(url)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }

}

inline fun getUrlStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener {
            function(it.toString())
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }

}

inline fun initUserModel(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(User::class.java) ?: User()
            if (USER.username.isEmpty()) {
                USER.username = UID
            }
            function()
        })
}

fun updatePhonesDatabase(contactsArray: ArrayList<DefaultModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
            it.children.forEach { dataSnapshot ->
                contactsArray.forEach { contact ->
                    if (dataSnapshot.key == contact.phone) {
                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)
                            .child(dataSnapshot.value.toString()).child(CHILD_ID)
                            .setValue(dataSnapshot.value.toString())
                            .addOnFailureListener {
                                showToast("Error")
                            }

                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)
                            .child(dataSnapshot.value.toString()).child(CHILD_FULLNAME)
                            .setValue(contact.fullname)
                            .addOnFailureListener {
                                showToast("Error")
                            }

                    }
                }

            }
        })
    }

}

fun DataSnapshot.getDefaultModel(): DefaultModel =
    this.getValue(DefaultModel::class.java) ?: DefaultModel()

fun DataSnapshot.getUserModel(): User = this.getValue(User::class.java) ?: User()
fun sendMessage(message: String, receivingId: String, typeText: String, function: () -> Unit) {

    val refDialogUser = "$NODE_MESSAGES/$UID/$receivingId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingId/$UID"

    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key

    val mapMessage = HashMap<String, Any>()
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = HashMap<String, Any>()

    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog).addOnSuccessListener {
        function()
    }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun updateCurrentUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME).setValue(newUserName)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                showToast("Обновлено")
                deleteOldUsername(newUserName)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}

private fun deleteOldUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnSuccessListener {
            showToast("Обновлено")
            ACTIVITY.supportFragmentManager.popBackStack()
            USER.username = newUserName
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun pushBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_BIO).setValue(newBio)
        .addOnSuccessListener {
            showToast("Данные обновлены")
            USER.bio = newBio
            ACTIVITY.supportFragmentManager.popBackStack()

        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun pushFullnameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_FULLNAME).setValue(fullname)
        .addOnSuccessListener {
            showToast("Успешно обновлено")
            USER.fullname = fullname
            ACTIVITY.mAppDrawer.updateHeader()
            ACTIVITY.supportFragmentManager.popBackStack()

        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun sendFile(
    receivingId: String,
    fileUrl: String,
    messageKey: String,
    messageType: String,
    filename: String
) {

    val refDialogUser = "$NODE_MESSAGES/$UID/$receivingId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingId/$UID"


    val mapMessage = HashMap<String, Any>()

    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TYPE] = messageType
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    val mapDialog = HashMap<String, Any>()

    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }

}

fun getMessageKey(id: String) =
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id).push().key.toString()

fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    receivedID: String,
    messageType: String,
    filename: String = ""
) {

    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(messageKey)

    putFileToStorage(uri, path) {
        getUrlStorage(path) {
            sendFile(receivedID, it, messageKey, messageType, filename)


        }
    }

}

fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mFile)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }

}

fun saveToMainList(id: String, type: String) {

    val refUser = "$NODE_MAIN_LIST/$UID/$id"
    val refReceived = "$NODE_MAIN_LIST/$id/$UID"

    val mapUser = HashMap<String, Any>()
    val mapReceived = HashMap<String, Any>()

    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = type

    mapReceived[CHILD_ID] = UID
    mapReceived[CHILD_TYPE] = type

    val defaultMap = HashMap<String, Any>()

    defaultMap[refUser] = mapUser
    defaultMap[refReceived] = mapReceived

    REF_DATABASE_ROOT.updateChildren(defaultMap)
        .addOnFailureListener { showToast(it.message.toString()) }

}

fun deleteChat(id: String, function: () -> Unit) {

    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(UID).child(id).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }

}

fun clearChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(id).child(UID)
                .removeValue()
                .addOnSuccessListener { function() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }

}

fun createGroupInDatabase(
    groupName: String,
    uri: Uri,
    contactsList: List<DefaultModel>,
    function: () -> Unit
) {
    val groupKey = REF_DATABASE_ROOT.child(NODE_GROUPS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_GROUPS).child(groupKey)
    val pathToStorage = REF_STORAGE_ROOT.child(GROUPS_IMAGE_FOLDER).child(groupKey)

    val dataMap = hashMapOf<String, Any>()
    dataMap[CHILD_ID] = groupKey
    dataMap[CHILD_FULLNAME] = groupName
    dataMap[CHILD_PHOTO_URL] = "empty"

    val membersMap = hashMapOf<String, Any>()
    contactsList.forEach {
        membersMap[it.id] = USER_MEMBER
    }
    membersMap[UID] = USER_CREATOR

    dataMap[NODE_MEMBERS] = membersMap

    path.updateChildren(dataMap)
        .addOnSuccessListener {
            function()
            if (uri != Uri.EMPTY) {
                putFileToStorage(uri, pathToStorage) {
                    getUrlStorage(pathToStorage) {

                        path.child(CHILD_PHOTO_URL).setValue(it)

                        addGroupToChats(dataMap, contactsList) {
                            function()
                        }
                    }
                }
            } else {
                addGroupToChats(dataMap, contactsList) {
                    function()
                }
            }
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }


}

fun addGroupToChats(
    dataMap: HashMap<String, Any>,
    contactsList: List<DefaultModel>,
    function: () -> Unit
) {

    val path = REF_DATABASE_ROOT.child(NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()

    map[CHILD_ID] = dataMap[CHILD_ID].toString()
    map[CHILD_TYPE] = TYPE_GROUP

    contactsList.forEach {
        path.child(it.id).child(map[CHILD_ID].toString()).updateChildren(map)
    }

    path.child(UID).child(map[CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageToGroup(message: String, groupID: String, typeText: String, function: () -> Unit) {

    var refMessages = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"
    val messageKey = REF_DATABASE_ROOT.child(refMessages).push().key

    val mapMessage = HashMap<String, Any>()

    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP



    REF_DATABASE_ROOT.child(refMessages).child(messageKey.toString()).updateChildren(mapMessage).addOnSuccessListener {
        function()
    }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendFileToGroup(
    groupId: String,
    fileUrl: String,
    messageKey: String,
    messageType: String,
    filename : String
) {

    val refMessages = "$NODE_GROUPS/$groupId/$NODE_MESSAGES"
    val key = REF_DATABASE_ROOT.child(refMessages).push().key


    val mapMessage = HashMap<String, Any>()

    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TYPE] = messageType
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename


    REF_DATABASE_ROOT.child(refMessages).child(key.toString()).updateChildren(mapMessage)
        .addOnFailureListener { showToast(it.message.toString()) }

}

fun uploadFileToStorageGroup(
    uri: Uri,
    messageKey: String,
    receivedID: String,
    messageType: String,
    filename: String = ""
) {

    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(messageKey)

    putFileToStorage(uri, path) {
        getUrlStorage(path) {
            sendFileToGroup(receivedID, it, messageKey, messageType, filename)


        }
    }

}
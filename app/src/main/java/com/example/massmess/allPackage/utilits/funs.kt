package com.example.massmess.allPackage.utilits

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.massmess.R
import com.example.massmess.allPackage.MainActivity
import com.example.massmess.allPackage.database.updatePhonesDatabase
import com.example.massmess.allPackage.models.DefaultModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

fun showToast(message: String) {
    Toast.makeText(ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun restartActivity() {
    val intent = Intent(ACTIVITY, MainActivity::class.java)
    ACTIVITY.startActivity(intent)
    ACTIVITY.finish()
}

fun switchFragment(fragment: Fragment, addToStack: Boolean = true) {
    if(addToStack){
        ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_container, fragment)
            .commit()
    }else {
        ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(R.id.data_container, fragment)
            .commit()
    }
}




fun closeKeyboard () {
    val manager : InputMethodManager = ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(ACTIVITY.window.decorView.windowToken, 0 )
}

fun ImageView.downloadAndSetImage(url : String) {
    Picasso.get()
        .load(url)
        .fit()
        .placeholder(R.drawable.ic_menu_contacts)
        .into(this)
}

fun initContacts() {
    if (checkPermission(READ_CONTACTS)) {
        var contactsArray = arrayListOf<DefaultModel>()
        val cursor = ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()) {
                val fullName =
                    it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val phone =
                    it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = DefaultModel()
                newModel.fullname = fullName
                newModel.phone = phone.replace(Regex("[\\s,-]"), "")
                contactsArray.add(newModel)
            }
        }

        cursor?.close()
        updatePhonesDatabase(contactsArray)
    }
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm" , Locale.getDefault())
    return timeFormat.format(time)
}

fun getFileNameFromUri(uri: Uri): String {
    var result = ""
    val cursor = ACTIVITY.contentResolver.query(uri , null , null , null , null)
    try {
        if (cursor != null && cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        }
    }catch (e:Exception) {
        showToast(e.message.toString())
    }finally {
        cursor?.close()
        return result
    }


}

fun getPlurals(count : Int) = ACTIVITY.resources.getQuantityString(
    R.plurals.members_count , count , count
)
package com.example.massmess.allPackage.ui.fragments.register

import androidx.fragment.app.Fragment
import com.example.massmess.R
import com.example.massmess.allPackage.database.AUTH
import com.example.massmess.allPackage.utilits.ACTIVITY
import com.example.massmess.allPackage.utilits.restartActivity
import com.example.massmess.allPackage.utilits.showToast
import com.example.massmess.allPackage.utilits.switchFragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_phone_number.*
import java.util.concurrent.TimeUnit


class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {

    private lateinit var mPhoneNumber: String
    private lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener(){
                    if (it.isSuccessful){
                        showToast("Добро пожаловать")
                        restartActivity()
                    }
                    else
                        showToast(it.exception?.message.toString())
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                switchFragment(EnterCodeFragment(mPhoneNumber , id))
            }

        }
        register_enter_phone_btn_next.setOnClickListener {
            sendCode()
        }


    }

    private fun sendCode() {
        if (register_input_phone.text.toString().isEmpty()) {
            showToast("Введите номер телефона")
        } else {
            authUser()
        }
    }

    private fun authUser() {
        mPhoneNumber = register_input_phone.text.toString()

        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder()
                .setPhoneNumber(mPhoneNumber)
                .setTimeout(60, TimeUnit.SECONDS)
                .setActivity(ACTIVITY)
                .setCallbacks(mCallback)
                .build()
        )
    }

}
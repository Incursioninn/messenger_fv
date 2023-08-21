package com.example.massmess.allPackage.utilits

import com.example.massmess.allPackage.database.*

enum class States(val state : String) {

    ONLINE("В сети"),
    OFFLINE("Не в сети"),
    TYPING("Печатает");

    companion object {
        fun updateState (states : States) {

            if (AUTH.currentUser != null) {
                REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_STATE)
                    .setValue(states.state)
                    .addOnSuccessListener { USER.state = states.state }
            }
        }

    }
}
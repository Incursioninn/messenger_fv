package com.example.massmess.allPackage.utilits

import android.media.MediaRecorder
import java.io.File

class AppVoiceRecorder {


    private val mMediaRecorder = MediaRecorder()
    private lateinit var mFile: File
    private lateinit var mMessageKey: String

    fun startRecording(messageKey: String) {
        try {
            mMessageKey = messageKey
            createFileForRecording()
            prepareMediaRecorder()
            mMediaRecorder.start()
        } catch (e: java.lang.Exception) {
            showToast(e.message.toString())
        }

    }

    private fun prepareMediaRecorder() {
        mMediaRecorder.apply {
            reset()
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            setOutputFile(mFile.absolutePath)
            prepare()
        }


    }

    private fun createFileForRecording() {
        mFile = File(ACTIVITY.filesDir, mMessageKey)
        mFile.createNewFile()
    }

    fun stopRecording(onSuccess: (file: File, messageKey: String) -> Unit) {
        try {
            mMediaRecorder.stop()
            onSuccess(mFile, mMessageKey)
        } catch (e: java.lang.Exception) {
            showToast(e.message.toString())
            mFile.delete()
        }

    }

    fun releaseRecorder() {
        try {
            mMediaRecorder.release()

        } catch (e: java.lang.Exception) {
            showToast(e.message.toString())
        }

    }

}


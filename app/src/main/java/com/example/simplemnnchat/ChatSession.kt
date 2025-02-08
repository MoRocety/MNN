package com.example.simplemnnchat

import android.util.Log
import java.io.File
import java.util.HashMap

class ChatSession {
    private var nativePtr: Long = 0
    private var mGenerating = false
    private var mReleaseRequested = false

    external fun initNative(configPath: String, useTmpPath: Boolean, history: List<String>?, isDiffusion: Boolean, isR1: Boolean): Long
    external fun submitNative(instanceId: Long, input: String, keepHistory: Boolean, listener: GenerateProgressListener): HashMap<String, Any>
    external fun resetNative(instanceId: Long)
    external fun releaseNative(instanceId: Long, isDiffusion: Boolean)

    fun create(configPath: String): Boolean {
        nativePtr = initNative(configPath, false, null, false, false)
        return nativePtr != 0L
    }

    fun generate(input: String, progressListener: GenerateProgressListener): HashMap<String, Any> {
        synchronized(this) {
            mGenerating = true
            val result = submitNative(nativePtr, input, true, progressListener)
            mGenerating = false
            if (mReleaseRequested) {
                releaseInner()
            }
            return result
        }
    }

    private fun releaseInner() {
        if (nativePtr != 0L) {
            releaseNative(nativePtr, false)
            nativePtr = 0
        }
    }

    fun release() {
        synchronized(this) {
            if (mGenerating) {
                mReleaseRequested = true
            } else {
                releaseInner()
            }
        }
    }

    companion object {
        init {
            System.loadLibrary("llm")
            System.loadLibrary("MNN_CL")
        }
    }
}

interface GenerateProgressListener {
    fun onProgress(progress: String): Boolean
}
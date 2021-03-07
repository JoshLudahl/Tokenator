package com.token.tokenator.utilities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import com.token.tokenator.R

object Clipuous {

    fun copyToClipboard(text: String, context: Context) {
        val clipboardManager = ContextCompat.getSystemService(
            context,
            ClipboardManager::class.java
        ) as ClipboardManager
        val clipData = ClipData.newPlainText(R.string.secret_sauce.toString(), text)
        clipboardManager.setPrimaryClip(clipData)
    }
}

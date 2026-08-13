package com.token.tokenator.utilities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.PersistableBundle
import androidx.core.content.ContextCompat
import com.token.tokenator.R

object Clipuous {
    fun copyToClipboard(
        text: String,
        context: Context,
        isSensitive: Boolean = false,
    ) {
        val clipboardManager =
            ContextCompat.getSystemService(
                context,
                ClipboardManager::class.java,
            ) as ClipboardManager
        val clipData = ClipData.newPlainText(R.string.secret_sauce.toString(), text)

        if (isSensitive) {
            clipData.description.extras = PersistableBundle().apply {
                putBoolean("android.content.extra.IS_SENSITIVE", true)
            }
        }

        clipboardManager.setPrimaryClip(clipData)
    }
}

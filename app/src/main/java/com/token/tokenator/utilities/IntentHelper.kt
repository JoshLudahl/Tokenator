package com.token.tokenator.utilities

import android.content.Context
import android.content.Intent

object IntentHelper {
    fun handleShareClick(
        token: String,
        context: Context,
    ) {
        val sendIntent =
            Intent(
                Intent.ACTION_SEND,
            ).apply {
                putExtra(Intent.EXTRA_TEXT, token)
                type = "text/plain"
            }

        val shareIntent = Intent.createChooser(sendIntent, null)

        context.startActivity(shareIntent)
    }
}

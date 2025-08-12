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

fun shareAppIntent(context: Context) {
    val packageName = context.packageName
    val shareText =
        "Check out Tokenator: https://play.google.com/store/apps/details?id=$packageName"
    val sendIntent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Tokenator")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    val chooser = Intent.createChooser(sendIntent, "Share Tokenator")
    context.startActivity(chooser, null)
}

package com.token.tokenator.utilities

import android.app.Activity
import android.graphics.Color
import android.view.View
import com.token.tokenator.R
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

object FeatureDiscovery {
    fun show(
        activity: Activity,
        description: String,
        title: String,
        view: View,
    ) {
        MaterialTapTargetPrompt.Builder(activity)
            .setTarget(view)
            .setBackgroundColour(
                activity.resources.getColor(
                    R.color.yellow,
                    activity.theme,
                ),
            )
            .setFocalColour(Color.BLACK)
            .setPrimaryText(title)
            .setSecondaryText(description)
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    prompt.dismiss()
                }
            }
            .show()
    }
}

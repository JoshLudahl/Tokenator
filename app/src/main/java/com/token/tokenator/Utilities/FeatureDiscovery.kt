package com.token.tokenator.Utilities

import android.app.Activity
import android.graphics.Typeface
import android.view.View
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.token.tokenator.R

object FeatureDiscovery {

    fun showFeature(
        activity: Activity,
        view: View,
        title: String,
        description: String
    ) {
        TapTargetView.showFor(activity,  // `this` is an Activity
            TapTarget.forView(
                view,
                title,
                description
            )
                .outerCircleColor(R.color.yellow)
                .outerCircleAlpha(0.96f)
                .targetCircleColor(R.color.white)
                .titleTextSize(20)
                .titleTextColor(R.color.white)
                .descriptionTextSize(14)
                .descriptionTextColor(R.color.yellow)
                .textColor(R.color.blackish)
                .textTypeface(Typeface.SANS_SERIF)
                .dimColor(R.color.black)
                .drawShadow(true)
                .cancelable(true)
                .tintTarget(true)
                .transparentTarget(true)
                .targetRadius(60),
            object : TapTargetView.Listener() {
                // The listener can listen for regular clicks, long clicks or cancels
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view) // This call is optional
                }

                override fun onOuterCircleClick(view: TapTargetView?) {
                    super.onOuterCircleClick(view)
                    view?.dismiss(true)
                }
            })
    }
}
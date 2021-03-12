package com.token.tokenator.settings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.token.tokenator.R
import com.token.tokenator.model.SettingsItem

@BindingAdapter("itemText")
fun TextView.setBackgroundColor(item: SettingsItem) {

    when (item.included) {
        true -> {
            setBackgroundResource(R.drawable.settings_item_active_background)
        }
        false -> {
            setBackgroundResource(R.drawable.settings_item_inactive_background)
            setTextColor(resources.getColor(R.color.yellow, context.theme))
        }
    }
}
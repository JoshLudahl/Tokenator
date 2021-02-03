package com.token.tokenator.settings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.token.tokenator.R
import com.token.tokenator.model.SettingsItem

@BindingAdapter("itemText")
fun TextView.setBackgroundColor(item: SettingsItem) {
    setBackgroundResource(when(item.included) {
        true -> R.drawable.settings_item_active_background
        false -> R.drawable.settings_item_inactive_background
    })
}
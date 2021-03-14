package com.token.tokenator.screens

import com.android.elk.espresso.click
import com.android.elk.espresso.on
import com.android.elk.espresso.view
import com.token.tokenator.R
import com.token.tokenator.model.Type

class MainScreen {

    fun selectSettingsButton() {

    }

    fun selectBookmarkButton() {

    }

    fun selectSwitch(type: Type) {

    }

    fun dragLengthButton() {

    }

    fun selectGenerate() {
        click on view(R.id.button_generate_token)
    }

    fun generatePasswordFieldIsNotEmpty() {

    }
}

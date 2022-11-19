package com.token.tokenator.screens

import androidx.test.espresso.action.ViewActions.typeText
import com.softklass.elk.espresso.click
import com.softklass.elk.espresso.confirm
import com.softklass.elk.espresso.into
import com.softklass.elk.espresso.isDisplayed
import com.softklass.elk.espresso.not
import com.softklass.elk.espresso.on
import com.softklass.elk.espresso.scrollTo
import com.softklass.elk.espresso.toastMatcher
import com.softklass.elk.espresso.view
import com.token.tokenator.R
import com.token.tokenator.model.Type

class MainScreen {

    fun selectSettingsButton() {
        click on view(R.id.settings_button)
    }

    fun selectBookmarkButton() {
        click on view(R.id.view_saved_button)
    }

    fun selectSwitch(vararg types: Type) {
        for (type in types) {
            click on when (type) {
                Type.LOWERCASE -> R.id.switch_lower_case
                Type.NUMERIC -> R.id.switch_numeric
                Type.SPECIAL -> R.id.switch_special_characters
                Type.UPPERCASE -> R.id.switch_uppercase
            }.let { view(it) }
        }
    }

    fun selectGenerate() {
        click on view(R.id.button_generate_token)
    }

    fun generateToastIsDisplayed() {
        toastMatcher(R.string.toast_copied_to_clipboard)
    }

    fun generatePasswordFieldIsNotEmpty() {
        with(view(R.id.generated_field)) {
            scrollTo()
            this confirm view("").not()
        }
    }

    fun enterTextIntoPasswordNameField(text: String) {
        with(view(R.id.token_name)) {
            scrollTo()
            typeText(text) into this
        }
    }

    fun selectSavedPasswordField() {
        with(view(R.id.save_button)) {
            scrollTo()
            click()
        }
    }

    fun settingsButtonIsDisplayed() = view(R.id.settings_button) confirm isDisplayed
}

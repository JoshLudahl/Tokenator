package com.token.tokenator.screens

import com.android.elk.espresso.*
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
        view(R.id.generated_field) confirm view("").not()
    }

    fun enterTextIntoPasswordNameField(text: String) {
        typeText(text) into view(R.id.token_name)
    }

    fun selectSavedPasswordField() {
        with(view(R.id.save_button)) {
            scrollTo()
            click()
        }
    }

    fun settingsButtonIsDisplayed() = view(R.id.settings_button) confirm isDisplayed
}

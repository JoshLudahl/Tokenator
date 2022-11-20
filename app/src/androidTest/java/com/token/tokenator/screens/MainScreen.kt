package com.token.tokenator.screens

import androidx.annotation.IdRes
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import com.softklass.elk.common.stringValue
import com.softklass.elk.common.targetContext
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
    enum class MainScreenElement(
        @IdRes val id: Int
    ) {
        BOOKMARK_BUTTON(R.id.view_saved_button),
        GENERATED_FIELD(R.id.generated_field),
        GENERATE_PASSWORD(R.id.button_generate_token),
        SAVE_BUTTON(R.id.save_button),
        SETTINGS_BUTTON(R.id.settings_button),
        SWITCH_LOWERCASE(R.id.switch_lower_case),
        SWITCH_NUMERIC(R.id.switch_numeric),
        SWITCH_SPECIAL_CHARACTERS(R.id.switch_special_characters),
        SWITCH_UPPERCASE(R.id.switch_uppercase),
        TOKEN_NAME(R.id.token_name),
    }

    fun selectSettingsButton() {
        click on view(MainScreenElement.SETTINGS_BUTTON.id)
    }

    fun selectBookmarkButton() {
        with(view(MainScreenElement.BOOKMARK_BUTTON.id)) {
            scrollTo()
            click on this
        }
    }

    fun selectSwitch(vararg types: Type) {
        for (type in types) {
            click on view(getSwitchTypeRes(type))
        }
    }

    fun selectGenerate() {
        click on view(MainScreenElement.GENERATE_PASSWORD.id)
    }

    fun generateToastIsDisplayed() {
        toastMatcher(R.string.toast_copied_to_clipboard)
    }

    fun generatePasswordFieldIsNotEmpty() {
        with(view(MainScreenElement.GENERATED_FIELD.id)) {
            scrollTo()
            this confirm view("").not()
        }
    }

    fun enterTextIntoPasswordNameField(text: String) {
        with(view(MainScreenElement.TOKEN_NAME.id)) {
            scrollTo()
            typeText(text) into this
        }
    }

    fun selectSavedPasswordField() {
        with(view(MainScreenElement.SAVE_BUTTON.id)) {
            scrollTo()
            click()
        }
    }

    fun settingsButtonIsDisplayed() = view(MainScreenElement.SETTINGS_BUTTON.id) confirm isDisplayed

    fun generatedFieldDoeNotContainType(type: Type) {
        targetContext.stringValue(getSwitchTypeRes(type))
        // TODO updated to check for exclusion
    }

    private fun getSwitchTypeRes(type: Type): Int {
        return when (type) {
            Type.LOWERCASE -> MainScreenElement.SWITCH_LOWERCASE
            Type.NUMERIC -> MainScreenElement.SWITCH_NUMERIC
            Type.SPECIAL -> MainScreenElement.SWITCH_SPECIAL_CHARACTERS
            Type.UPPERCASE -> MainScreenElement.SWITCH_UPPERCASE
        }.id
    }
}

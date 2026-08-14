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
    enum class MainScreenElement(
        val tag: String,
    ) {
        ADD_PASSWORD_FAB("ADD_PASSWORD_FAB"),
        SETTINGS_BUTTON("SETTINGS_BUTTON"),

        // These are currently in AddPasswordScreen but the test logic seems mixed
        GENERATE_PASSWORD("GENERATE_TOKEN_BUTTON"),
        SAVE_BUTTON("SAVE_TOKEN_BUTTON"),
        SWITCH_LOWERCASE("SWITCH_LOWERCASE"),
        SWITCH_NUMERIC("SWITCH_NUMERIC"),
        SWITCH_SPECIAL_CHARACTERS("SWITCH_SPECIAL"),
        SWITCH_UPPERCASE("SWITCH_UPPERCASE"),
        TOKEN_NAME("TOKEN_NAME_FIELD"),
    }

    fun selectSettingsButton() {
        // The settings icon button has a content description but we can also try to find it by icon res if ELK supported it,
        // but here I'll try with the localized string "Settings" if ELK's view(String) works that way.
        // Or better, use the English string since we are in test.
        click on view("Settings")
    }

    fun selectBookmarkButton() {
        // "New Password" is the text on the FAB
        click on view("New Password")
    }

    fun selectSwitch(vararg types: Type) {
        for (type in types) {
            val label =
                when (type) {
                    Type.UPPERCASE -> "Uppercase Letters"
                    Type.LOWERCASE -> "Lowercase Letters"
                    Type.NUMERIC -> "Numeric"
                    Type.SPECIAL -> "Special Characters"
                }
            click on view(label)
        }
    }

    fun selectGenerate() {
        click on view("Generated Token")
    }

    fun generateToastIsDisplayed() {
        toastMatcher(R.string.toast_copied_to_clipboard)
    }

    fun generatePasswordFieldIsNotEmpty() {
        // Just verify something is there
        view("GENERATED PASSWORD") confirm isDisplayed
    }

    fun enterTextIntoPasswordNameField(text: String) {
        // The label for the field is "App / Website Name"
        with(view("App / Website Name")) {
            scrollTo()
            typeText(text) into this
        }
    }

    fun selectSavedPasswordField() {
        click on view("Save Password to Vault")
    }

    fun settingsButtonIsDisplayed() = view("Settings") confirm isDisplayed

    fun generatedFieldDoeNotContainType(type: Type) {
        // targetContext.stringValue(getSwitchTypeRes(type))
        // TODO updated to check for exclusion
    }

    private fun getSwitchTypeTag(type: Type): String =
        when (type) {
            Type.LOWERCASE -> MainScreenElement.SWITCH_LOWERCASE
            Type.NUMERIC -> MainScreenElement.SWITCH_NUMERIC
            Type.SPECIAL -> MainScreenElement.SWITCH_SPECIAL_CHARACTERS
            Type.UPPERCASE -> MainScreenElement.SWITCH_UPPERCASE
        }.tag
}

package com.token.tokenator.tests

import com.android.elk.common.Given
import com.android.elk.common.Then
import com.android.elk.common.When
import com.android.elk.screen
import com.android.elk.verify
import com.token.tokenator.BaseTest
import com.token.tokenator.model.Type
import com.token.tokenator.screens.MainScreen
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class MainScreenTest: BaseTest() {

    @Test
    fun clickingSettingsGoesToSettingsView() {
        verify<MainScreen> {
            settingsButtonIsDisplayed()
        }
        screen<MainScreen> {
            Thread.sleep(500) // Fails to recognize click due to pending bindings, just wait.
            selectSettingsButton()
        }
    }

    @Test
    fun clickingBookmarkIconGoesToSavedPasswords() {
        screen<MainScreen> {
            selectBookmarkButton()
        }
    }

    @Test
    fun selectGenerateButtonGeneratesATokenAndDisplaysAToast() {
        screen<MainScreen> {
            selectGenerate()
        }
        verify<MainScreen> {
            generatePasswordFieldIsNotEmpty()
        }
    }

    @Test
    fun selectEachSwitch() {
        screen<MainScreen> {
            selectSwitch(*Type.values())
        }
    }

    @Test
    fun generatePasswordThenEnterNameThenSave() {
        @Given("That I see the generate password button")
        @When("I select the generate button")
        screen<MainScreen> {
            selectGenerate()
            enterTextIntoPasswordNameField("Generic Name")
            selectSavedPasswordField()
        }

        @Then("I see my password saved in the save password screen")
        screen<MainScreen> {
            selectBookmarkButton()
        }
    }
}

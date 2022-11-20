package com.token.tokenator.tests

import com.softklass.elk.common.And
import com.softklass.elk.common.Given
import com.softklass.elk.common.Then
import com.softklass.elk.common.When
import com.softklass.elk.screen
import com.softklass.elk.verify
import com.token.tokenator.BaseTest
import com.token.tokenator.model.Type
import com.token.tokenator.screens.MainScreen
import com.token.tokenator.screens.SavePasswordScreen
import com.token.tokenator.screens.SettingsScreen
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class MainScreenTest : BaseTest() {

    @Before
    fun before() {
        @Given("I am on the main screen")
        screen<MainScreen> {
            // TODO verify main screen
        }
    }

    @Test
    fun clickingSettingsGoesToSettingsView() {
        verify<MainScreen> {
            settingsButtonIsDisplayed()
        }

        @When("I select the settings button")
        screen<MainScreen> {
            Thread.sleep(500) // Fails to recognize click due to pending bindings, just wait.
            selectSettingsButton()
        }

        @Then("I am taken to the settings screen")
        screen<SettingsScreen> {
            // TODO verify setting screen is displayed
        }
    }

    @Test
    fun clickingBookmarkIconGoesToSavedPasswords() {
        @When("I select the bookmark button")
        screen<MainScreen> {
            Thread.sleep(500)
            selectBookmarkButton()
        }

        @Then("I am taken to the saved password screen")
        screen<SavePasswordScreen> {
            // TODO verify saved screen is displayed
        }
    }

    @Test
    fun selectGenerateButtonGeneratesATokenAndDisplaysAToast() {
        @When("I select the generate button")
        screen<MainScreen> {
            selectGenerate()
        }

        @Then("the password generated field is not empty")
        verify<MainScreen> {
            generatePasswordFieldIsNotEmpty()
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

    @Test
    fun selectTheNumericSwitch() {
        with(Type.NUMERIC) {
            screen<MainScreen> {
                @When("I select the numeric switch")
                selectSwitch(this@with)

                @And("I select the generate password button")
                selectGenerate()

                @Then("I see the generated password doesn't contain numeric characters")
                generatedFieldDoeNotContainType(this@with)
            }
        }
    }

    @Test
    fun selectTheUppercaseSwitch() {
        with(Type.UPPERCASE) {
            screen<MainScreen> {
                @When("I select the uppercase switch")
                selectSwitch(this@with)

                @And("I select the generate password button")
                selectGenerate()

                @Then("I see the generated password doesn't contain numeric characters")
                generatedFieldDoeNotContainType(this@with)
            }
        }
    }

    @Test
    fun selectTheLowercaseSwitch() {
        with(Type.LOWERCASE) {
            screen<MainScreen> {
                @When("I select the lowercase switch")
                selectSwitch(this@with)

                @And("I select the generate password button")
                selectGenerate()

                @Then("I see the generated password doesn't contain numeric characters")
                generatedFieldDoeNotContainType(this@with)
            }
        }
    }

    @Test
    fun selectTheSpecialCharacterSwitch() {
        with(Type.SPECIAL) {
            screen<MainScreen> {
                @When("I select the special character switch")
                selectSwitch(this@with)

                @And("I select the generate password button")
                selectGenerate()

                @Then("I see the generated password doesn't contain numeric characters")
                generatedFieldDoeNotContainType(this@with)
            }
        }
    }
}

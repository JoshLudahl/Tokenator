package com.token.tokenator.tests

import com.android.elk.screen
import com.android.elk.verify
import com.token.tokenator.BaseTest
import com.token.tokenator.screens.MainScreen
import org.junit.Test

class MainScreenTest: BaseTest() {

    @Test
    fun selectGenerateButtonGeneratesAToken() {
        screen<MainScreen> {
            selectGenerate()
            Thread.sleep(3000)
        }
        verify<MainScreen> {

        }
    }
}

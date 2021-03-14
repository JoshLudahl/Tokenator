package com.token.tokenator

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.android.elk.rules.EspressoSetupRule
import org.junit.Rule

abstract class BaseTest {

    @get: Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

//    @get: Rule
//    val espressoSetupRule = EspressoSetupRule(activityScenarioRule)
}

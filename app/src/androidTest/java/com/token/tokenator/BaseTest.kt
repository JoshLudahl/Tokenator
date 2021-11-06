package com.token.tokenator

import android.app.Application
import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain

@HiltAndroidTest
abstract class BaseTest {

    val hiltAndroidRule = HiltAndroidRule(this)
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val ruleChain = RuleChain.outerRule(hiltAndroidRule).around(activityScenarioRule)

    @Before
    fun init() = hiltAndroidRule.inject()
}

class Runner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}

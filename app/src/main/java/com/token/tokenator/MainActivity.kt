package com.token.tokenator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.token.tokenator.R.id.myNavHostFragment
import com.token.tokenator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(myNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

    }
}

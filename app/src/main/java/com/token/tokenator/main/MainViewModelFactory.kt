package com.token.tokenator.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
          return MainViewModel(application) as T
      }
        throw java.lang.IllegalArgumentException("Unknown ViewModel Class")
    }
}

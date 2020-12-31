package com.token.tokenator.savedpassword

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.token.tokenator.R

class SavedTokenFragment : Fragment() {

    companion object {
        fun newInstance() = SavedTokenFragment()
    }

    private lateinit var viewModel: SavedTokenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.saved_token_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SavedTokenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
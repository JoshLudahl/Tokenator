package com.token.tokenator.savedpassword

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.token.tokenator.R
import com.token.tokenator.databinding.SavedTokenFragmentBinding

class SavedTokenFragment : Fragment(R.layout.saved_token_fragment) {

    private lateinit var viewModel: SavedTokenViewModel
    private lateinit var binding: SavedTokenFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(SavedTokenViewModel::class.java)
        binding = DataBindingUtil.bind(view)!!
        binding.tokenViewModel = viewModel

        binding.buttonBack.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.myNavHostFragment).navigateUp()
        }


    }
}

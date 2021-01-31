package com.token.tokenator.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.token.tokenator.R
import com.token.tokenator.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: SettingsFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SettingsFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setUpListeners()

    }

    private fun setUpListeners() {

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
        }

    }
}

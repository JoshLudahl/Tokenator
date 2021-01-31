package com.token.tokenator.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.token.tokenator.R
import com.token.tokenator.databinding.SettingsFragmentBinding
import com.token.tokenator.model.SettingsItem

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: SettingsFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SettingsFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setUpListeners()

        val list: List<SettingsItem> = retrieveList()
        val adapter = SettingsAdapter(list, SettingsListener {
            onItemClick()
        })

        binding.specialCharactersRecyclerview.apply {
            this.adapter = adapter
            this.layoutManager = GridLayoutManager(requireContext(), 5)
        }

    }

    private fun onItemClick() {

    }

    private fun setUpListeners() {

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
        }
    }

    private fun retrieveList(): List<SettingsItem> {
        val specialChars = listOf(33, 35, 36, 37, 38, 39, 40, 41, 42, 43, 45, 46, 47, 60, 61, 62, 63, 64)
        val list: MutableList<SettingsItem> = mutableListOf()

        specialChars.forEach {
            list.add(SettingsItem(it.toChar().toString(), true))
        }
        return list
    }
}

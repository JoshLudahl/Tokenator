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
import com.token.tokenator.model.Type
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: SettingsFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SettingsFragmentBinding.bind(view)
        binding.viewModel = viewModel

        val adapter = SettingsAdapter(SettingsListener {
            onItemClick(it)
        })

        setUpRecyclerView(adapter)
        setUpListeners()
        setupObservers(adapter)
    }

    private fun setUpRecyclerView(adapter: SettingsAdapter) {
        binding.specialCharactersRecyclerview.apply {
            this.adapter = adapter
            this.layoutManager = GridLayoutManager(requireContext(), 5)
        }
    }

    private fun onItemClick(item: SettingsItem) {
        // TODO: 2/2/21 Implement action next
    }

    private fun setupObservers(adapter: SettingsAdapter) {
        viewModel.specialCharList.observe(viewLifecycleOwner, {
            it?.let {
                adapter.setItems(it)
            }
        })

        binding.lifecycleOwner = this
    }

    private fun setUpListeners() {

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun retrieveList(): List<SettingsItem> {
        val specialChars =
            listOf(33, 35, 36, 37, 38, 39, 40, 41, 42, 43, 45, 46, 47, 60, 61, 62, 63, 64)
        val list: MutableList<SettingsItem> = mutableListOf()

        specialChars.forEach {
            list.add(
                SettingsItem(
                    item = it.toChar().toString(),
                    included = true,
                    category = Type.SPECIAL
                )
            )
        }
        return list
    }
}

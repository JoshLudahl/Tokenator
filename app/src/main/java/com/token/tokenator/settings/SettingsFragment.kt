package com.token.tokenator.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.token.tokenator.R
import com.token.tokenator.utilities.DataPref
import com.token.tokenator.databinding.SettingsFragmentBinding
import com.token.tokenator.di.DataStoreNoRepeat
import com.token.tokenator.model.SettingsItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings_fragment) {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    @DataStoreNoRepeat
    @Inject
    lateinit var noRepeat: String

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val viewModel: SettingsViewModel by viewModels()
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SettingsFragmentBinding.bind(view)
        binding.viewModel = viewModel

        val adapter = SettingsAdapter(SettingsListener {
            onItemClick(it)
        })

        setUpRecyclerView(adapter)
        setUpListeners()
        setupObservers(adapter)

        lifecycleScope.launchWhenStarted {
            binding
                .noRepeatCharactersSwitch
                .isChecked = (DataPref.readDataStore(noRepeat, dataStore) ?: true)
                .toString()
                .toBoolean()
        }
    }

    private fun setUpRecyclerView(adapter: SettingsAdapter) {
        binding.specialCharactersRecyclerview.apply {
            this.adapter = adapter
            this.layoutManager = GridLayoutManager(requireContext(), 6)
        }
    }

    private fun onItemClick(item: SettingsItem) {
        Log.i("ITEM", item.included.toString())

        item.included = !item.included
        viewModel.updateItems(item)
    }

    private fun setupObservers(adapter: SettingsAdapter) {
        viewModel.specialCharList.observe(viewLifecycleOwner, {
            it?.let {
                adapter.setItems(it)
            }
        })

        binding.lifecycleOwner = this.viewLifecycleOwner
    }

    private fun setUpListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.noRepeatCharactersSwitch.setOnClickListener {
            lifecycleScope.launch {
                DataPref.saveDataStore(
                    noRepeat,
                    binding.noRepeatCharactersSwitch.isChecked,
                    dataStore
                )
            }
        }
    }
}

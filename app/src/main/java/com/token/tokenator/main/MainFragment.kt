package com.token.tokenator.main

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.token.tokenator.R
import com.token.tokenator.Utilities.Clipuous
import com.token.tokenator.Utilities.FeatureDiscovery
import com.token.tokenator.databinding.MainFragmentBinding
import com.token.tokenator.model.Tokenator
import com.token.tokenator.model.Type
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment) {

    lateinit var dataStore: DataStore<Preferences>
    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = MainFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonGenerateToken.setOnClickListener {
            generatePassword()
        }

        binding.saveButton.setOnClickListener {
            saveToken()

        }

        binding.viewSavedButton.setOnClickListener {
            navigateToSaved()
        }

        binding.fluidSlider

        binding.tokenName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.saveButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 0) {
                    binding.saveButton.visibility = View.GONE
                }
            }
        })

        dataStore = view.context.createDataStore(name = "settings")
    }

    private fun navigateToSaved() {
        Navigation.findNavController(
            requireActivity(),
            R.id.myNavHostFragment
        ).navigate(R.id.action_mainFragment_to_savedTokenFragment)
    }

    private fun saveToken() {
        when {
            binding.tokenName.text.isNullOrEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    R.string.error_enter_name_for_password,
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.generatedField.text.isNullOrEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    R.string.error_generate_password_first,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                viewModel.insert(
                    passwordName = binding.tokenName.editableText.toString().trim(),
                    token = binding.generatedField.text.toString()
                )
                Toast.makeText(requireContext(), R.string.password_saved, Toast.LENGTH_SHORT).show()
                binding.tokenName.text?.clear()

                lifecycleScope.launch {
                    val preferenceItem = readDataStore("feature_discovery") ?: ""
                    if (preferenceItem == "null") {
                        Log.i("FEATURE?", "$preferenceItem: Showing feature because it has not been shown.")
                        showFeature()
                        saveDataStore("feature_discovery", true)
                    } else {
                        Log.i("FEATURE", "Skipping feature because it has been shown.")
                    }

                }
            }
        }
    }

    private suspend fun saveDataStore(key: String, value: Boolean) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = value.toString()
        }
    }

    private suspend fun readDataStore(key: String): String {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey].toString()
    }

    private fun copyToClipBoard(password: String) {
        Clipuous.copyToClipboard(password, requireContext())
        Toast.makeText(requireContext(), R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT)
            .show()
    }

    private fun generatePassword() {

        val chars = mutableListOf<Type>()
        if (binding.switchLowerCase.isChecked) chars.add(Type.LOWERCASE)
        if (binding.switchNumeric.isChecked) chars.add(Type.NUMERIC)
        if (binding.switchSpecialCharacters.isChecked) chars.add(Type.SPECIAL)
        if (binding.switchUppercase.isChecked) chars.add(Type.UPPERCASE)

        var length: Int = (binding.fluidSlider.position * 100).toInt()
        if (length == 0) {
            binding.fluidSlider.position = 0.08f
            length = 8
        }
        val password = Tokenator.generate(
            length,
            chars
        )

        when {
            password.isNotEmpty() -> {
                viewModel.setToken(password)
                binding.generatedField.text = password
                viewModel.setLength(binding.fluidSlider.position)

                copyToClipBoard(password)

                binding.tokenName.apply {
                    visibility = View.VISIBLE
                    requestFocus()
                    viewModel.tokenNameEditText = View.VISIBLE
                }
            }
            else -> Toast.makeText(
                requireContext(),
                R.string.toast_length_warning,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showFeature() {
        binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
        hideKeyboard(requireActivity())
        FeatureDiscovery.showFeature(
            requireActivity(),
            binding.viewSavedButton,
            getString(R.string.feature_view_saved_passwords_title),
            getString(R.string.feature_view_saved_passwords_description)
        )
    }
}

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

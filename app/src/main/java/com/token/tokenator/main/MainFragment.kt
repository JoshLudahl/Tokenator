package com.token.tokenator.main

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.token.tokenator.R
import com.token.tokenator.Utilities.Clipuous
import com.token.tokenator.Utilities.FeatureDiscovery
import com.token.tokenator.Utilities.Tokenator
import com.token.tokenator.database.settingsitem.PopulateDatabase
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.databinding.MainFragmentBinding
import com.token.tokenator.di.*
import com.token.tokenator.model.Type
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment) {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    @DataStoreCharacterPopulation
    @Inject
    lateinit var characterPopulation: String

    @DataStoreFeature
    @Inject
    lateinit var feature: String

    @DataStoreLowercase
    @Inject
    lateinit var lowercase: String

    @DataStoreNoRepeat
    @Inject
    lateinit var noRepeat: String

    @DataStoreNumeric
    @Inject
    lateinit var numeric: String

    @DataStoreSpecialCharacters
    @Inject
    lateinit var specialCharacters: String

    @DataStoreUppercase
    @Inject
    lateinit var uppercase: String

    @Inject
    lateinit var settingsItemRepository: SettingsItemRepository

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var doesNotRepeat by Delegates.notNull<Boolean>()

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = MainFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        lifecycleScope.launchWhenStarted {
            viewModel.shouldShowEasterEggToast.collect {
                when (it) {
                    true -> {
                        viewModel.setShouldShowToastToFalse()
                        showToast("You make touch")
                    }
                }
            }
        }

        populateSettingsItem()

        binding.buttonGenerateToken.setOnClickListener {
            lifecycleScope.launch {
                generatePassword()
            }
        }

        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
        }

        binding.saveButton.setOnClickListener {
            saveToken()
        }

        binding.viewSavedButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_savedTokenFragment)
        }

        binding.switchUppercase.setOnClickListener {
            lifecycleScope.launch {
                saveDataStore(uppercase, binding.switchUppercase.isChecked)
            }
        }

        binding.switchLowerCase.setOnClickListener {
            lifecycleScope.launch {
                saveDataStore(lowercase, binding.switchLowerCase.isChecked)
            }
        }

        binding.switchNumeric.setOnClickListener {
            lifecycleScope.launch {
                saveDataStore(numeric, binding.switchNumeric.isChecked)
            }
        }

        binding.switchSpecialCharacters.setOnClickListener {
            lifecycleScope.launch {
                saveDataStore(specialCharacters, binding.switchSpecialCharacters.isChecked)
            }
        }

        binding.generatedField.setOnLongClickListener { view ->
            if (viewModel.token.value.isNotEmpty()) {
                copyToClipBoard(viewModel.token.value)
                showToast("Copied to clipboard")
            }
            true
        }

        lifecycleScope.launchWhenStarted {
            binding.apply {
                switchLowerCase.isChecked = readDataStore(lowercase).toBoolean()
                switchNumeric.isChecked = readDataStore(numeric).toBoolean()
                switchSpecialCharacters.isChecked = readDataStore(specialCharacters).toBoolean()
                switchUppercase.isChecked = readDataStore(uppercase).toBoolean()
            }
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
                if (s?.trim()?.length == 0) {
                    binding.saveButton.visibility = View.GONE
                }
            }
        })

        lifecycleScope.launchWhenStarted {
            viewModel.noRepeatFlow.collect { repeatable ->
                doesNotRepeat = repeatable
            }
        }
    }

    private fun populateSettingsItem() {
        lifecycleScope.launch {
            val preferenceItem = readDataStore(characterPopulation)
            if (preferenceItem == "null") {
                Log.i("SP", "Populating Characters into the database")
                PopulateDatabase.populateDatabase(settingsItemRepository)
                saveDataStore(characterPopulation, true)
                saveDataStore(lowercase, true)
                saveDataStore(noRepeat, false)
                saveDataStore(numeric, true)
                saveDataStore(specialCharacters, true)
                saveDataStore(uppercase, true)
            }
        }
    }

    private fun saveToken() {
        when {
            binding.tokenName.text.isNullOrEmpty() -> {
                showToast(getString(R.string.error_enter_name_for_password))
            }

            binding.generatedField.text.isNullOrEmpty() -> {
                showToast(getString(R.string.error_generate_password_first))
            }

            else -> {
                viewModel.insert(
                    passwordName = binding.tokenName.editableText.toString().trim(),
                    token = binding.generatedField.text.toString()
                )
                showToast(getString(R.string.password_saved))
                binding.tokenName.text?.clear()

                lifecycleScope.launch {
                    val preferenceItem = readDataStore(feature)
                    if (preferenceItem == "null") {
                        Log.i(
                            "FEATURE?",
                            "$preferenceItem: Showing feature because it has not been shown."
                        )
                        showFeature()
                        saveDataStore(feature, true)

                    } else {
                        Log.i("FEATURE", "Skipping feature because it has been shown.")
                    }
                }
            }
        }
    }

    private suspend fun saveDataStore(key: String, value: Boolean) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = value.toString()
        }
    }

    private suspend fun readDataStore(key: String): String {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey].toString()
    }

    private fun copyToClipBoard(password: String) {
        Clipuous.copyToClipboard(password, requireContext())
        showToast(getString(R.string.toast_copied_to_clipboard))
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

        val stringList = mutableListOf<String>()
        viewModel.allCharacters.value?.forEach {
            if (it.included.not()) stringList.add(it.item)
        }

        val password = Tokenator.generate(
            length = length,
            includes = chars,
            excludedCharacters = stringList,
            doNotRepeat = doesNotRepeat
        )

        when {
            password.isNotEmpty() -> {

                viewModel.apply {
                    setToken(password)
                    setLength(binding.fluidSlider.position)
                    setTokenNameEditTextLabelVisible()
                    setTokenNameEditTextFieldVisibility()
                }

                binding.apply {
                    generatedField.text = password
                    tokenName.requestFocus()
                }

                copyToClipBoard(password)
            }
            else -> showToast(getString(R.string.toast_length_warning))
        }
    }

    private fun showFeature() {
        binding.scrollView.scrollTo(0, 0)
        hideKeyboard(requireActivity())

        FeatureDiscovery.showFeature(
            requireActivity(),
            binding.viewSavedButton,
            getString(R.string.feature_view_saved_passwords_title),
            getString(R.string.feature_view_saved_passwords_description)
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

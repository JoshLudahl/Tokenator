package com.token.tokenator.ui.main

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.token.tokenator.R
import com.token.tokenator.database.settingsitem.PopulateDatabase
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.databinding.MainFragmentBinding
import com.token.tokenator.di.DataStoreCharacterPopulation
import com.token.tokenator.di.DataStoreFeature
import com.token.tokenator.di.DataStoreLowercase
import com.token.tokenator.di.DataStoreNoRepeat
import com.token.tokenator.di.DataStoreNumeric
import com.token.tokenator.di.DataStorePassPhrase
import com.token.tokenator.di.DataStorePassPhraseIncluded
import com.token.tokenator.di.DataStoreSpecialCharacters
import com.token.tokenator.di.DataStoreUppercase
import com.token.tokenator.model.Type
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.DataPref
import com.token.tokenator.utilities.FeatureDiscovery
import com.token.tokenator.utilities.IntentHelper
import com.token.tokenator.utilities.Tokenator
import com.token.tokenator.utilities.alert.GenericAlert
import dagger.hilt.android.AndroidEntryPoint
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

    @DataStorePassPhrase
    @Inject
    lateinit var passPhrase: String

    @DataStorePassPhraseIncluded
    @Inject
    lateinit var passPhraseIncluded: String

    @Inject
    lateinit var settingsItemRepository: SettingsItemRepository

    @Inject
    lateinit var tokenRepository: TokenRepository

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var doesNotRepeat by Delegates.notNull<Boolean>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = MainFragmentBinding.bind(view)
        binding.viewModel = viewModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shouldShowEasterEggToast.collect {
                    when (it) {
                        true -> {
                            viewModel.setShouldShowToastToFalse()
                            showToast("You make touch")
                        }
                        else -> Unit
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                populateSettingsItem()
            }
        }

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

        binding.generatedField.setOnClickListener { view ->
            if (viewModel.token.value.isNotEmpty()) {
                copyToClipBoard(viewModel.token.value)
                showToast("Copied to clipboard")
            }
        }

        binding.shareTokenButton.setOnClickListener {
            IntentHelper.handleShareClick(viewModel.token.value, requireContext())
        }

        binding.privacyPolicyText.setOnClickListener {
            GenericAlert(R.string.privacy_policy)
                .show(parentFragmentManager, "Privacy Policy")
        }

        binding.tokenName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    if (count > 0) {
                        binding.saveButton.visibility = View.VISIBLE
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.trim()?.length == 0) {
                        binding.saveButton.visibility = View.GONE
                    }
                }
            },
        )

        binding.uppercaseContainer.setOnClickListener {
            toggleSwitch(Type.UPPERCASE)
        }

        binding.lowercaseContainer.setOnClickListener {
            toggleSwitch(Type.LOWERCASE)
        }

        binding.numericContainer.setOnClickListener {
            toggleSwitch(Type.NUMERIC)
        }

        binding.specialCharacterContainer.setOnClickListener {
            toggleSwitch(Type.SPECIAL)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.noRepeatFlow.collect { repeatable ->
                    doesNotRepeat = repeatable
                }
            }
        }
    }

    private fun toggleSwitch(type: Type) {
        with(viewModel) {
            when (type) {
                Type.NUMERIC -> {
                    switchNumeric
                }
                Type.LOWERCASE -> {
                    switchLowerCase
                }
                Type.SPECIAL -> {
                    switchSpecialCharacter
                }
                Type.UPPERCASE -> {
                    switchUpperCase
                }
            }.let {
                saveSwitchState(type, !it.value)
            }
        }
    }

    private fun populateSettingsItem() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val preferenceItem = readDataStore(characterPopulation)
                if (preferenceItem == "null") {
                    Log.i("SP", "Populating Characters into the database")
                    PopulateDatabase.populateDatabase(settingsItemRepository)
                    DataPref.saveDataStore(characterPopulation, true, dataStore)
                }
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
                    token = binding.generatedField.text.toString(),
                    login = binding.tokenLoginName.text?.toString(),
                )
                showToast(getString(R.string.password_saved))
                binding.tokenName.text?.clear()
                binding.tokenLoginName.text?.clear()

                lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        val preferenceItem = readDataStore(feature)
                        if (preferenceItem == "null") {
                            Log.i(
                                "FEATURE?",
                                "$preferenceItem: Showing feature because it has not been shown.",
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
    }

    private suspend fun saveDataStore(
        key: String,
        value: Boolean,
    ) {
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

    private suspend fun generatePassword() {
        val chars = mutableListOf<Type>()
        val mapOfTypes =
            mapOf(
                binding.switchLowerCase to Type.LOWERCASE,
                binding.switchNumeric to Type.NUMERIC,
                binding.switchSpecialCharacters to Type.SPECIAL,
                binding.switchUppercase to Type.UPPERCASE,
            )

        mapOfTypes.forEach { item ->
            if (item.key.isChecked) chars.add(item.value)
        }

        var length: Int = (binding.fluidSlider.value).toInt()

        val stringList = mutableListOf<String>()
        viewModel.allCharacters.value.forEach {
            if (it.included.not()) stringList.add(it.item)
        }

        val passphrase =
            when (
                (DataPref.readDataStore(key = passPhraseIncluded, dataStore) ?: true)
                    .toString()
                    .toBoolean()
            ) {
                true -> binding.viewModel?.passphrase?.value?.phrase
                else -> null
            }
        val password =
            Tokenator.generate(
                length = length,
                includesTypesList = chars,
                excludedCharacters = stringList,
                doNotRepeat = doesNotRepeat,
                passphrase ?: "",
            )

        when {
            password.isNotEmpty() -> {
                viewModel.apply {
                    setToken(password)
                    setLength(binding.fluidSlider.value)
                    setTokenNameEditTextLabelVisible()
                    setTokenNameEditTextFieldVisibility()
                }

                binding.apply {
                    generatedField.text = password
                    tokenName.requestFocus()
                }
            }
            else -> showToast(getString(R.string.toast_length_warning))
        }
    }

    private fun showFeature() {
        binding.scrollView.scrollTo(0, 0)
        hideKeyboard(requireActivity())

        FeatureDiscovery.show(
            activity = requireActivity(),
            view = binding.viewSavedButton,
            title = getString(R.string.feature_view_saved_passwords_title),
            description = getString(R.string.feature_view_saved_passwords_description),
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT,
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

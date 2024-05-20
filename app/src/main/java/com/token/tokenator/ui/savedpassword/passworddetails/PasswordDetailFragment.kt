package com.token.tokenator.ui.savedpassword.passworddetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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
import com.token.tokenator.databinding.PasswordDetailFragmentBinding
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.FeatureDiscovery
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PasswordDetailFragment : Fragment(R.layout.password_detail_fragment) {
    companion object {
        const val SHARE_FEATURE = "share_feature"
    }

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    private val viewModel: PasswordDetailViewModel by viewModels()
    private var _binding: PasswordDetailFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = PasswordDetailFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val token = arguments?.getString("token").toString().toIntOrNull()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                token?.let {
                    viewModel.getToken(it)
                }
            }
        }

        binding.buttonUpdatePassword.setOnClickListener {
            token?.let {
                if (binding.buttonUpdatePassword.isEnabled) {
                    viewModel.insert(
                        login = binding.tokenLoginName.text.toString().trim(),
                        token = binding.tokenPassword.text.toString(),
                        passwordName = binding.tokenName.text.toString().trim(),
                    )
                    Toast.makeText(requireContext(), R.string.password_saved, Toast.LENGTH_SHORT)
                        .show()
                }
            }
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
                ) {}

                override fun afterTextChanged(s: Editable?) {
                    binding.buttonUpdatePassword.isEnabled = s?.trim()?.length != 0
                }
            },
        )

        binding.loginTextField.setEndIconOnClickListener {
            copyToClipBoard(binding.tokenLoginName.text.toString())
        }

        binding.passwordTextField.setEndIconOnClickListener {
            copyToClipBoard(binding.tokenPassword.text.toString())
        }

        setUpListeners()
        handleFeature()
    }

    private fun setUpListeners() {
        binding.shareButton.setOnClickListener {
            viewModel.token.value?.token?.let { token ->
                requireContext().handleShareClick(token)
            }
        }
    }

    private fun Context.handleShareClick(token: String) {
        val sendIntent =
            Intent(
                Intent.ACTION_SEND,
            ).apply {
                putExtra(Intent.EXTRA_TEXT, token)
                type = "text/plain"
            }

        val shareIntent = Intent.createChooser(sendIntent, null)

        startActivity(shareIntent)
    }

    private fun copyToClipBoard(text: String) {
        Clipuous.copyToClipboard(text, requireContext())
        showToast(getString(R.string.toast_copied_to_clipboard))
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun handleFeature() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val preferenceItem = readDataStore(SHARE_FEATURE)
                if (preferenceItem == "null") {
                    Log.i(
                        "FEATURE?",
                        "$preferenceItem: Showing feature because it has not been shown.",
                    )
                    showFeature()
                    saveDataStore(SHARE_FEATURE, true)
                } else {
                    Log.i("FEATURE", "Skipping feature because it has been shown.")
                }
            }
        }
    }

    private fun showFeature() {
        FeatureDiscovery.show(
            activity = requireActivity(),
            view = binding.shareButton,
            title = getString(R.string.feature_view_share_password_title),
            description = getString(R.string.feature_view_share_password_description),
        )
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
}
